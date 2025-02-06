package com.beyond.StomachForce.youngjae.review.dtos;

import com.beyond.StomachForce.youngjae.review.entity.Review;
import com.beyond.StomachForce.youngjae.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewRes {
    private Long id; //     리뷰아이디
    private String contents;
    private List<?> reviewPhotoUrl;
    private Double rating;

//    public static ReviewRes fromEntity(Review review) {
//        return ReviewRes.builder()
//                .id(review.getId())
//                .contents(review.getContents())
//                .rating(Double.valueOf(review.getRating().getValue()))
//                .reviewPhotoUrl(review.getReviewPhotos().stream()
//                        .map(reviewPhoto -> reviewPhoto.getPhotoUrl()).collect(Collectors.toList()))
//                .build();
//    }
}
