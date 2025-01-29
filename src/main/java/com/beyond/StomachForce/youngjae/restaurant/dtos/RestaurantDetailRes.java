package com.beyond.StomachForce.youngjae.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantDetailRes {
    private Long id;
    private String name;            //가게명
    private String description;     //설명
    private String phoneNumber;     //전번
    private String address;         //주소
    private Double averageRating;   //별점
    private Long bookmarkCount;     //좋아요한 사람 수
}
