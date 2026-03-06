package com.eduflow.eduflow.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find all published courses for students browsing
    List<Course> findByPublishedTrue();

    Optional<Course> findByIdAndInstructorEmail(Long id, String email);

    // Find all courses by an instructor
    List<Course> findByInstructorId(Long instructorId);

    // Search courses by title
    List<Course> findByTitleContainingIgnoreCaseAndPublishedTrue(String title);

    // Fetch course with sections in one query (avoids N+1)
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.sections WHERE c.id = :id")
    Optional<Course> findByIdWithSections(@Param("id") Long id);

    Page<Course> findByPublishedTrue(Pageable pageable);

    Page<Course> findByPublishedTrueAndTitleContainingIgnoreCase(
            String keyword, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.published = true "
            + "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:category IS NULL OR c.category = :category) "
            + "AND (:level IS NULL OR c.level = :level)")
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("level") String level,
            Pageable pageable);

}
