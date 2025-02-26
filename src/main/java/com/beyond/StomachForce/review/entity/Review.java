package com.beyond.StomachForce.review.entity;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.review.converter.RatingConverter;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.dtos.ReviewRes;
import com.beyond.StomachForce.review.dtos.ReviewUpdateReq;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String contents;                          // 내용
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReviewStatus reviewStatus = ReviewStatus.ACTIVE;



    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;                           //customer id랑 합쳐야함

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;                   //restaurant id와 fk

    @ManyToOne
    @JoinColumn(name = "reservation_id")  // 예약
    private Reservation reservation;

    @OneToMany(mappedBy = "review",cascade = CascadeType.ALL) // 사진 넣으면 자동으로 리뷰에 추가됨
    private List<ReviewPhoto> reviewPhotos = new ArrayList<>();

    public Review(User user, Restaurant restaurant, Rating rating, @NotBlank(message = "비울 수 없는 항목입니다.") String contents) {
        super();
    }

    public ReviewRes fromEntity(Review review) {
        List<String> imagePaths = this.reviewPhotos.isEmpty()
                ? List.of("/assets/noImage.jpg")
                : this.reviewPhotos.stream().map(ReviewPhoto::getReviewImagePath).toList();

        return ReviewRes.builder()
                .id(this.id)
                .contents(this.contents)
                .rating((double) this.rating.getValue())
                .reviewPhotoUrl(imagePaths)
                .build();
    }

    public ReviewListRes toListDto() {

        List<String> imagePaths = this.reviewPhotos.isEmpty()
                ? List.of("/assets/noImage.jpg")
                : this.reviewPhotos.stream().map(rp -> rp.getReviewImagePath()).toList();

        return ReviewListRes.builder()
                .id(this.id)
                .contents(this.contents)
                .RestaurantName(this.restaurant.getName())
                .userIdentify(this.user.getIdentify())
                .rating(this.rating.getValue())
                .reviewPhotos(imagePaths)
                .createdTime(this.getCreatedTime())
                .updatedTime(this.getUpdatedTime())
                .build();
    }

    public void updateReview(String contents, Rating rating) {
        if (contents != null && !contents.isBlank()) {
            this.contents = contents;
        }
        if (rating != null) {
            this.rating = rating;
        }
    }





}
