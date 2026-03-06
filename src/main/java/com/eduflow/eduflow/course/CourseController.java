package com.eduflow.eduflow.course;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eduflow.eduflow.common.response.ApiResponse;
import com.eduflow.eduflow.course.dto.CourseRequest;
import com.eduflow.eduflow.course.dto.CourseResponse;
import com.eduflow.eduflow.course.dto.LessonRequest;
import com.eduflow.eduflow.course.dto.LessonResponse;
import com.eduflow.eduflow.course.dto.SectionRequest;
import com.eduflow.eduflow.course.dto.SectionResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ─── PUBLIC ENDPOINTS ───────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success("Courses fetched",
                courseService.getAllPublishedCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Course fetched",
                courseService.getCourse(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> searchCourses(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search results",
                courseService.searchCourses(keyword)));
    }

    // ─── INSTRUCTOR ENDPOINTS ───────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created",
                        courseService.createCourse(request, userDetails.getUsername())));
    }

    @PostMapping("/{id}/thumbnail")   // ← was @PutMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> uploadThumbnail(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Thumbnail uploaded",
                courseService.uploadThumbnail(id, file, userDetails.getUsername())));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> publishCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Course published",
                courseService.publishCourse(id, userDetails.getUsername())));
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Courses fetched",
                courseService.getInstructorCourses(userDetails.getUsername())));
    }

    // ─── SECTION ENDPOINTS ──────────────────────────────────

    @PostMapping("/{courseId}/sections")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<SectionResponse>> addSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Section added",
                        courseService.addSection(courseId, request, userDetails.getUsername())));
    }

    // ─── LESSON ENDPOINTS ───────────────────────────────────

    @PostMapping("/sections/{sectionId}/lessons")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<LessonResponse>> addLesson(
            @PathVariable Long sectionId,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson added",
                        courseService.addLesson(sectionId, request, userDetails.getUsername())));
    }

    @PostMapping("/lessons/{lessonId}/content")   // ← was @PutMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<LessonResponse>> uploadLessonContent(
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Content uploaded",
                courseService.uploadLessonContent(lessonId, file, userDetails.getUsername())));
    }
}