package com.eduflow.eduflow.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eduflow.eduflow.common.exception.ResourceNotFoundException;
import com.eduflow.eduflow.course.Course;
import com.eduflow.eduflow.course.CourseRepository;
import com.eduflow.eduflow.user.User;
import com.eduflow.eduflow.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentEventProducer eventProducer;

    public EnrollmentResponse enrollStudent(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new RuntimeException("Already enrolled in this course");
        }

        if (!course.isPublished()) {
            throw new RuntimeException("Course is not available for enrollment");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        enrollmentRepository.save(enrollment);

        eventProducer.sendEnrollmentEvent(EnrollmentEvent.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .studentEmail(student.getEmail())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .build());

        return mapToResponse(enrollment);
    }

    public List<EnrollmentResponse> getMyEnrollments(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return enrollmentRepository.findByStudentId(student.getId())
                .stream().map(this::mapToResponse).toList();
    }

    public boolean isEnrolled(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseThumbnail(enrollment.getCourse().getThumbnailUrl())
                .enrolledAt(enrollment.getEnrolledAt())
                .completed(enrollment.isCompleted())
                .build();
    }
}