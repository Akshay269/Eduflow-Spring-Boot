package com.eduflow.eduflow.review;

import com.eduflow.eduflow.common.exception.ResourceNotFoundException;
import com.eduflow.eduflow.common.response.PageResponse;
import com.eduflow.eduflow.course.Course;
import com.eduflow.eduflow.course.CourseRepository;
import com.eduflow.eduflow.enrollment.EnrollmentRepository;
import com.eduflow.eduflow.review.dto.ReviewRequest;
import com.eduflow.eduflow.review.dto.ReviewResponse;
import com.eduflow.eduflow.user.User;
import com.eduflow.eduflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public ReviewResponse createReview(Long courseId, ReviewRequest request, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // must be enrolled
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new RuntimeException("You must be enrolled to review this course");
        }

        // one review per student per course
        if (reviewRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new RuntimeException("You have already reviewed this course");
        }

        Review review = Review.builder()
                .student(student)
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
        updateCourseRating(course);

        return mapToResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getStudent().getEmail().equals(email)) {
            throw new RuntimeException("You can only update your own review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);
        updateCourseRating(review.getCourse());

        return mapToResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getStudent().getEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own review");
        }

        Course course = review.getCourse();
        reviewRepository.delete(review);
        updateCourseRating(course);
    }

    public PageResponse<ReviewResponse> getCourseReviews(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PageResponse.from(
                reviewRepository.findByCourseId(courseId, pageable)
                        .map(this::mapToResponse));
    }

    public ReviewResponse getMyReview(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return reviewRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    // ─── PRIVATE ────────────────────────────────────────────

    private void updateCourseRating(Course course) {
        Double avg = reviewRepository.calculateAverageRating(course.getId());
        long count = reviewRepository.countByCourseId(course.getId());

        course.setAverageRating(avg != null
                ? BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        course.setTotalReviews((int) count);
        courseRepository.save(course);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .studentId(review.getStudent().getId())
                .studentName(review.getStudent().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}