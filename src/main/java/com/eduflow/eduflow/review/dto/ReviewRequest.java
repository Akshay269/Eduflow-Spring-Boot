package com.eduflow.eduflow.review.dto;



import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}