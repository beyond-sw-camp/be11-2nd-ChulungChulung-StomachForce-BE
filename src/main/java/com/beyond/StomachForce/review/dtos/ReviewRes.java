package com.beyond.StomachForce.review.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewRes {
    private Long id; //     리뷰아이디
    private String contents;
    private List<?> reviewPhotoUrl;
    private Double rating;


}
