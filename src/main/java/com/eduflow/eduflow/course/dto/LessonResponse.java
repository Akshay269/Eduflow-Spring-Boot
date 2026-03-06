package com.eduflow.eduflow.course.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse  {
   
    private Long id;
    private String title;
    private String contentUrl;
    private String contentType;
    private Integer duration;
    private Integer orderIndex;
    private boolean free;
}