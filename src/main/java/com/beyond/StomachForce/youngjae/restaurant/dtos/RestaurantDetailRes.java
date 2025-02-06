package com.beyond.StomachForce.youngjae.restaurant.dtos;

import com.beyond.StomachForce.youngjae.restaurant.entity.Bookmark;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantAddress;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantPhoto;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.AlcoholSelling;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.DepositAvailable;
import com.beyond.StomachForce.youngjae.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantDetailRes {
    private Long id;
    private String name;            //가게명
    private String email;
    private String description;     //설명
    private String phoneNumber;     //전번
    private String address;         //주소
    private Double averageRating;   //별점
    private Long bookmarkCount;     //좋아요한 사람 수
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

}
