package com.beyond.StomachForce.restaurant.domain;


import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.restaurant.dtos.RestaurantDetailRes;
import com.beyond.StomachForce.restaurant.dtos.RestaurantListRes;
import com.beyond.StomachForce.restaurant.dtos.RestaurantUpdateReq;
import com.beyond.StomachForce.restaurant.domain.select.AlcoholSelling;
import com.beyond.StomachForce.restaurant.domain.select.DepositAvailable;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantRole;
import com.beyond.StomachForce.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
public class Restaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                             // 고유id

    @Column(unique = true, nullable = false)
    private String name;                         // 레스토랑 아름

    @Column(unique = true, nullable = false)
    private String registrationNumber;           // 사업자등록번호

    @Column(nullable = false)                    // 비밀번호
    private String password;

    @Column(nullable = false, unique = true)     // 이메일
    private String email;

    private String phoneNumber;                  // 가게 연락처

    @Enumerated(EnumType.STRING)
    private AlcoholSelling alcoholSelling;       // 주류판매여부

    @Enumerated(EnumType.STRING)
    private RestaurantRole role;                 // rolr

    @Column(nullable = false, length = 3000)
    private String description;                  // 가게 설명

    @Enumerated(EnumType.STRING)
    private DepositAvailable depositAvailable;   // 예약금 여부

    private Long deposit;                        //예약금

    @Column(nullable = false)
    private LocalDateTime openingTime;           // 여는 시간

    @Column(nullable = false)
    private LocalDateTime closingTime;           // 닫는 시간

    private LocalDateTime breakTime;             // 브레이크 타임

    @Column(nullable = false)
    private LocalDateTime lastOrder;             // 라스트 오더

    private LocalDate holiday;                   // 휴무일

    private Integer capacity;                    // 최대 수용 인원

    private Integer rating;                      // 별점

    private LocalDateTime updatedTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_address_id")  // Restaurant 테이블이 외래 키를 가짐
    private RestaurantAddress address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public RestaurantListRes listDtoFromEntity() {
        double averageRating = reviews.isEmpty() ? 0.0 : reviews.stream().mapToDouble
                (r -> r.getRating().getValue()).average().orElse(0.0);
        return RestaurantListRes.builder()
                .id(this.id)                                            // 레스토랑 Id
                .name(this.name)                                        // 레스토랑 이름
                .averageRating(averageRating)                           // 레스토랑 평균 별점
                .bookmarkCount((long) this.bookmarks.size())            // 레스토랑 즐겨찾기 한 사람 수
                .address(this.address.getFullAddress())
                .build();
    }

    public RestaurantDetailRes detailFromEntity() {
        double averageRating = reviews.isEmpty() ? 0.0 : reviews.stream().mapToDouble
                (r -> r.getRating().getValue()).average().orElse(0.0);
        return RestaurantDetailRes.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .description(this.description)
                .phoneNumber(this.phoneNumber)
                .address(this.address.getFullAddress())
                .averageRating(averageRating)
                .bookmarkCount(this.bookmarks.stream().count())
                .updatedTime(this.updatedTime)
                .build();
    }

    public void updateProfile(RestaurantUpdateReq dto,String password){
        if(dto.getName() != null) this.name = dto.getName();
        if(dto.getEmail() != null) this.email = dto.getEmail();
        if(dto.getPassword() != null) this.password = password;
        if(dto.getPhoneNumber() != null) this.phoneNumber = dto.getPhoneNumber();
        if(dto.getDescription() != null) this.description = dto.getDescription();
        if(dto.getOpeningTime() != null) this.openingTime = dto.getOpeningTime();
        if(dto.getClosingTime() != null) this.closingTime = dto.getClosingTime();
        if(dto.getLastOrder() != null) this.lastOrder=dto.getLastOrder();
        if(dto.getHoliday() != null) this.holiday = dto.getHoliday();
        if(dto.getCapacity() != 0) this.capacity = dto.getCapacity();
        if(dto.getAddress() != null) {
            if(this.address == null) {
                this.address = new RestaurantAddress(); // 기존 주소가 없으면 새로 생성하는 것
            }
            if(dto.getAddress().getCity() != null) this.address.setCity(dto.getAddress().getCity());
            if(dto.getAddress().getStreet() != null) this.address.setStreet(dto.getAddress().getStreet());
        }
    }





}
