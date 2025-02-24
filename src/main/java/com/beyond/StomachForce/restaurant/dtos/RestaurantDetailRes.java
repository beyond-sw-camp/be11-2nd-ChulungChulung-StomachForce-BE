package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.select.AlcoholSelling;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.*;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantDetailRes {
    private Long id;
    private String name;                //가게명
    private String email;               //가게 이메일
    private String description;         //설명
    private LocalDateTime openingTime;      // 여는시간
    private LocalDateTime closingTime;      // 닫는시간
    private LocalDateTime lastOrder;        // 라콜
    private String phoneNumber;         //전번
    private LocalDateTime breakTimeStart;   //브레이크 시작
    private LocalDateTime breakTimeEnd;     //브레이크 끝
    private Long deposit;               //예약금
    private String alcoholSelling;      // 주류 판매 여부
    private String restaurantType;      // 레스토랑 종류(한중일식)
    //휴무일 --> list로 변경 예정
    // 레스토랑 리뷰 리스트
    private Long bookmarksCount;        // 즐겨찾기 수
    // 메뉴 list

    private String address;         //주소
    private Double averageRating;   //별점
    private Long bookmarkCount;     //좋아요한 사람 수
    private List<String> imagePath; // 카리나 사진~~

    private LocalDateTime updatedTime; // 생성시간

}
