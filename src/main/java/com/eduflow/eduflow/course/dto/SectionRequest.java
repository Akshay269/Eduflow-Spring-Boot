package com.eduflow.eduflow.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SectionRequest {
    @NotBlank
    private String title;
    private Integer orderIndex;
}