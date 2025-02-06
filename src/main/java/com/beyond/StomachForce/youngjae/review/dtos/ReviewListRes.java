package com.beyond.StomachForce.youngjae.review.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewListRes {
    private Long id;
    private String title;
    private String contents;
    private String memberEmail;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
