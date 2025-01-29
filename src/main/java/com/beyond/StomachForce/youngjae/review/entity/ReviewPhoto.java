package com.beyond.StomachForce.youngjae.review.entity;

import com.beyond.StomachForce.youngjae.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
public class ReviewPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "photo_url")
    private String photoUrl;        //  리뷰 사진, 사진 안넣을 수도 있으니 널러블은 뺏습니다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;          // 레스토랑 리뷰 페이지랑 FK설정

}
