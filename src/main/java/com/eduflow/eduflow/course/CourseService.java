package com.eduflow.eduflow.course;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eduflow.eduflow.common.exception.ResourceNotFoundException;
import com.eduflow.eduflow.common.service.S3Service;
import com.eduflow.eduflow.course.dto.CourseRequest;
import com.eduflow.eduflow.course.dto.CourseResponse;
import com.eduflow.eduflow.course.dto.LessonRequest;
import com.eduflow.eduflow.course.dto.LessonResponse;
import com.eduflow.eduflow.course.dto.SectionRequest;
import com.eduflow.eduflow.course.dto.SectionResponse;
import com.eduflow.eduflow.user.User;
import com.eduflow.eduflow.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;


    // ─── COURSE CRUD ────────────────────────────────────────
    public CourseResponse createCourse(CourseRequest request, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .instructor(instructor)
                .build();

        return mapToCourseResponse(courseRepository.save(course));
    }

   
    public CourseResponse uploadThumbnail(Long courseId, MultipartFile file, String email) {
        Course course = getCourseAndVerifyOwnership(courseId, email);
        String url = s3Service.uploadFile(file, "thumbnails");
        course.setThumbnailUrl(url);
        return mapToCourseResponse(courseRepository.save(course));
    }

 
    public CourseResponse publishCourse(Long courseId, String email) {
        Course course = getCourseAndVerifyOwnership(courseId, email);
        course.setPublished(true);
        return mapToCourseResponse(courseRepository.save(course));
    }

    public CourseResponse getCourse(Long courseId) {
        Course course = courseRepository.findByIdWithSections(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return mapToCourseResponse(course);
    }

    public List<CourseResponse> getAllPublishedCourses() {
         
        return courseRepository.findByPublishedTrue()
                .stream().map(this::mapToCourseResponse).toList();
    }

    public List<CourseResponse> getInstructorCourses(String email) {
        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return courseRepository.findByInstructorId(instructor.getId())
                .stream().map(this::mapToCourseResponse).toList();
    }

    public List<CourseResponse> searchCourses(String keyword) {
        return courseRepository.findByTitleContainingIgnoreCaseAndPublishedTrue(keyword)
                .stream().map(this::mapToCourseResponse).toList();
    }

    // ─── SECTION CRUD ────────────────────────────────────────
    public SectionResponse addSection(Long courseId, SectionRequest request, String email) {
        Course course = getCourseAndVerifyOwnership(courseId, email);

        Section section = Section.builder()
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .course(course)
                .build();

        return mapToSectionResponse(sectionRepository.save(section));
    }

    // ─── LESSON CRUD ────────────────────────────────────────
    public LessonResponse addLesson(Long sectionId, LessonRequest request, String email) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        // Verify the instructor owns the course this section belongs to
        getCourseAndVerifyOwnership(section.getCourse().getId(), email);

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .contentType(request.getContentType())
                .duration(request.getDuration())
                .orderIndex(request.getOrderIndex())
                .free(request.isFree())
                .section(section)
                .build();

        return mapToLessonResponse(lessonRepository.save(lesson));
    }

    public LessonResponse uploadLessonContent(Long lessonId, MultipartFile file, String email) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        getCourseAndVerifyOwnership(lesson.getSection().getCourse().getId(), email);

        String url = s3Service.uploadFile(file, "lessons");
        lesson.setContentUrl(url);
        return mapToLessonResponse(lessonRepository.save(lesson));
    }

    // ─── HELPERS ────────────────────────────────────────────
    private Course getCourseAndVerifyOwnership(Long courseId, String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getInstructor().getEmail().equals(email)) {
            throw new RuntimeException("You don't have permission to modify this course");
        }
        return course;
    }






    private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .published(course.isPublished())
                .instructorName(course.getInstructor().getName())
                .instructorId(course.getInstructor().getId())
                .sections(course.getSections() != null // ← add null check
                        ? course.getSections().stream()
                                .map(this::mapToSectionResponse).toList()
                        : new ArrayList<>())
                .createdAt(course.getCreatedAt())
                .build();
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentUrl())
                .contentType(lesson.getContentType())
                .duration(lesson.getDuration())
                .orderIndex(lesson.getOrderIndex())
                .free(lesson.isFree())
                .build();
    }

    private SectionResponse mapToSectionResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .orderIndex(section.getOrderIndex())
                .lessons(section.getLessons() != null // ← add null check
                        ? section.getLessons().stream()
                                .map(this::mapToLessonResponse).toList()
                        : new ArrayList<>())
                .build();
    }

}
