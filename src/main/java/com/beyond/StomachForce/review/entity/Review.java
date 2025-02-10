package com.beyond.StomachForce.review.entity;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.review.converter.RatingConverter;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.dtos.ReviewRes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter

public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Convert(converter = RatingConverter.class)
    @Builder.Default
    private Rating rating = Rating.FIVE;              // 별점
    @Column(nullable = false, length = 3000)
    private String contents;                         // 내용


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;                           //customer id랑 합쳐야함

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;                   //restaurant id와 fk

    @OneToMany(mappedBy = "review",cascade = CascadeType.ALL) // 사진 넣으면 자동으로 리뷰에 추가됨
    private List<ReviewPhoto> reviewPhotos = new ArrayList<>();

    public ReviewRes fromEntity(Review review) {
        return ReviewRes.builder()
                .id(review.getId())
                .contents(review.getContents())
                .rating(Double.valueOf(review.getRating().getValue()))
                .reviewPhotoUrl(review.getReviewPhotos().stream()
                        .map(reviewPhoto -> reviewPhoto.getReviewImagePath()).collect(Collectors.toList()))
                .build();
    }



}
