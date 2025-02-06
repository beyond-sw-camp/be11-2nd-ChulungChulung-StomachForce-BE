package com.beyond.StomachForce.youngjae.review.entity;

import com.beyond.StomachForce.youngjae.common.entity.BaseTimeEntity;
import com.beyond.StomachForce.youngjae.restaurant.entity.Restaurant;
import com.beyond.StomachForce.youngjae.review.converter.RatingConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private Rating rating;              // 별점
    @Column(nullable = false, length = 3000)
    private String contents;            // 내용


//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    private Customer customer;      //customer id랑 합쳐야함

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;      //restaurant id와 fk

    @OneToMany(mappedBy = "review",cascade = CascadeType.ALL) // 사진 넣으면 자동으로 리뷰에 추가됨
    private List<ReviewPhoto> reviewPhotos = new ArrayList<>();


}
