package com.eduflow.eduflow.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double calculateAverageRating(@Param("courseId") Long courseId);

    long countByCourseId(Long courseId);
}