package com.beyond.StomachForce.restaurant.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantPhotoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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
    private String photoUrl;                // 사진url

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;          // 레스토랑 페이지랑 FK설정

    @Enumerated(EnumType.STRING)
    @Builder.Default                        // 기본적으로 활성화 시키기
    private RestaurantPhotoStatus photoStatus = RestaurantPhotoStatus.ACTIVE;

    public RestaurantPhoto(String photoUrl, Restaurant restaurant) {
        this.photoUrl = photoUrl;
        this.restaurant = restaurant;
    }


    public void photoDeactivate() {
        this.photoStatus = RestaurantPhotoStatus.INACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantPhoto that = (RestaurantPhoto) o;
        return Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl);
    }



}
