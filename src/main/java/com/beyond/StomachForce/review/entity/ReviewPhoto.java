package com.beyond.StomachForce.review.entity;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.review.dtos.ReviewPhotoRes;
import com.beyond.StomachForce.review.dtos.ReviewRes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
public class ReviewPhoto extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "photo_url")
    private String reviewImagePath;        //  리뷰 사진, 사진 안넣을 수도 있으니 널러블은 뺏습니다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;          // 레스토랑 리뷰 페이지랑 FK설정

    public ReviewPhoto(Review review, String s3Url) {
        this.reviewImagePath = s3Url;
        this.review = review;
    }

    public ReviewPhotoRes fromEntity(User user) {
        return ReviewPhotoRes.builder()
                .photoUrl(this.reviewImagePath)
                .user(user)
                .build();
    }

}
