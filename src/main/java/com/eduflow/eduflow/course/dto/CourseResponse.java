package com.eduflow.eduflow.course.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse implements Serializable  {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private boolean published;
    private String instructorName;
    private Long instructorId;
    private List<SectionResponse> sections;
    private LocalDateTime createdAt;
}