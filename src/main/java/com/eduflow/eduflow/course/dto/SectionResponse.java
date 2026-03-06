package com.eduflow.eduflow.course.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private List<LessonResponse> lessons;
}
