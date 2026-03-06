package com.eduflow.eduflow.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank
    private String title;
    private String contentType;
    private Integer duration;
    private Integer orderIndex;
    private boolean free;
}