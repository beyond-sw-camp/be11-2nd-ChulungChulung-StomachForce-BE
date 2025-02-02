package com.beyond.StomachForce.youngjae.restaurant.dtos;

import com.beyond.StomachForce.youngjae.restaurant.entity.Bookmark;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantAddress;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantPhoto;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.AlcoholSelling;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.DepositAvailable;
import com.beyond.StomachForce.youngjae.review.entity.Review;
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
public class RestaurantDetailResToAdmin {
    private Long id;
    private String name;                         //가게명
    private String registrationNumber;           // 사업자등록번호
    private String email;                        // 이메일
    private AlcoholSelling alcoholSelling;       // 주류판매여부
    private DepositAvailable depositAvailable;   // 예약금 여부
    private Long deposit;                        // 예약금
    private LocalDateTime openingTime;           // 여는 시간
    private LocalDateTime closingTime;           // 닫는 시간
    private LocalDateTime breakTime;             // 브레이크 타임
    private LocalDateTime lastOrder;             // 라스트 오더
    private LocalDate holiday;                   // 휴무일
    private int capacity;                        // 최대 수용 인원
    private String phoneNumber;                  // 전번
    private String description;                  // 설명
    private RestaurantAddress address;           // 주소
    private Double averageRating;                // 별점
    private Long bookmarkCount;                  // 좋아요한 사람 수
    private List<RestaurantPhoto> photos = new ArrayList<>();       // 사진
    private List<Review> reviews = new ArrayList<>();;              // 리뷰
    private List<Bookmark> bookmarks = new ArrayList<>();           // 즐겨찾기 한 사람
}
