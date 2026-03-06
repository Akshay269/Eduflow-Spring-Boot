package com.eduflow.eduflow.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseRequest {
    @NotBlank
    private String title;
    private String description;
    private BigDecimal price;
}