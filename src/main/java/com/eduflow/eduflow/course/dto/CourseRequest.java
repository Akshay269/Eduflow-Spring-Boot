package com.eduflow.eduflow.course.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    @NotBlank
    private String title;

    private String description;
    private String category;   // ← add
    private String level;      // ← add
    private BigDecimal price;
}