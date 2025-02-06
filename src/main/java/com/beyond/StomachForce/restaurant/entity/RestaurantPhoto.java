package com.beyond.StomachForce.restaurant.entity;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
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
public class RestaurantPhoto extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // 사진 고유 번호

    @Column(nullable = false,name = "photo_url")
    private String photoUrl;                //사진 이걸 어떻게 할깝쇼...ㅠㅠ url이믄 string입니다요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;          // 레스토랑 페이지랑 FK설정

}
