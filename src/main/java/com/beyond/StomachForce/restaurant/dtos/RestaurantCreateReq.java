package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.RestaurantAddress;
import com.beyond.StomachForce.restaurant.domain.RestaurantConvenience;
import com.beyond.StomachForce.restaurant.domain.RestaurantPhoto;
import com.beyond.StomachForce.restaurant.domain.select.AlcoholSelling;
import com.beyond.StomachForce.restaurant.domain.select.DepositAvailable;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantRole;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
//  이름 이메일 사업자등록증 비번, 가게 연락처, 주류판매여부, 예약금 여부, 예약금,
public class RestaurantCreateReq {
    @NotEmpty
    private String name;                        // 가게 이름

    @NotEmpty
    private String registrationNumber;          // 사업자등록증

    @NotEmpty
    @Size(min = 8)
    private String password;                    // 비번

    @NotEmpty
    private String email;                       // 이메일

    @NotEmpty
    private String phoneNumber;                  // 가게 전화번호

    @NotNull
    private AlcoholSelling alcoholSelling;       // 알콜 판매 여부

    @NotEmpty
    private String description;                  // 가게설명

    @NotNull
    private DepositAvailable depositAvailable;   // 예약금 여부

    private Long deposit;                        // 예약금

    @NotNull
    private LocalTime openingTime;           // 여는 시간

    @NotNull
    private LocalTime closingTime;           // 닫는 시간

    private LocalTime breakTimeStart;        // 브레이크 타임 시작

    private LocalTime breakTimeEnd;        // 브레이크 타임 끗!

    @NotNull
    private LocalTime lastOrder;             // 라스트 오더

    private LocalDate holiday;                   // 휴무일

    private int capacity;                       // 최대 수용 인원

    @NotNull
    private RestaurantType restaurantType;              // 한중일식 넣기

    @NotNull
    private RestaurantAddress address;                // 주소

//    private List<RestaurantConvenience> convenience;   //편의사항

    @NotEmpty
    private List<MultipartFile> restaurantPhotos = new ArrayList<>();        // 가게 사진 1장 이상

    public Restaurant toEntity(String encodedPassword) {
        return Restaurant.builder()
                .name(this.name)
                .registrationNumber(this.registrationNumber)
                .password(encodedPassword)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .alcoholSelling(this.alcoholSelling)
                .description(this.description)
                .depositAvailable(this.depositAvailable)
                .deposit(this.deposit)
                .openingTime(this.openingTime)
                .closingTime(this.closingTime)
                .breakTimeStart(this.breakTimeStart)
                .breakTimeEnd(this.breakTimeEnd)
                .lastOrder(this.lastOrder)
                .holiday(this.holiday)
                .capacity(this.capacity)
                .address(this.address)
//                .conveniences(this.convenience)
                .photos(new ArrayList<>())
                .restaurantType(this.restaurantType)
                .build();
    }

}
