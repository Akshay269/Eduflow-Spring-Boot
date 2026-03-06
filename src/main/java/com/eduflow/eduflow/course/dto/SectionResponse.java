package com.eduflow.eduflow.course.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private Integer orderIndex;
    private List<LessonResponse> lessons;
}
