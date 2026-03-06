package com.eduflow.eduflow.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find all published courses for students browsing
    List<Course> findByPublishedTrue();

    // Find all courses by an instructor
    List<Course> findByInstructorId(Long instructorId);

    // Search courses by title
    List<Course> findByTitleContainingIgnoreCaseAndPublishedTrue(String title);

    // Fetch course with sections in one query (avoids N+1)
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.sections WHERE c.id = :id")
    Optional<Course> findByIdWithSections(@Param("id") Long id);
}
