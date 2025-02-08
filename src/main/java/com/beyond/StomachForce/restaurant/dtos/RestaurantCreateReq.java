package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.RestaurantAddress;
import com.beyond.StomachForce.restaurant.domain.RestaurantPhoto;
import com.beyond.StomachForce.restaurant.domain.select.AlcoholSelling;
import com.beyond.StomachForce.restaurant.domain.select.DepositAvailable;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantRole;
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
    private LocalDateTime openingTime;           // 여는 시간

    @NotNull
    private LocalDateTime closingTime;           // 닫는 시간

    private LocalDateTime breakTime;             // 브레이크 타임

    @NotNull
    private LocalDateTime lastOrder;             // 라스트 오더

    private LocalDate holiday;                   // 휴무일

    private int capacity;                       // 최대 수용 인원


    private String restaurantRole;              // ROLE

    private String restaurantStatus;            // 활성화 상태

    @NotNull
    private RestaurantAddress address;           // 주소

//    @NotEmpty
//    private List<MultipartFile> restaurantPhotos;        // 가게 사진 1장 이상

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
                .breakTime(this.breakTime)
                .lastOrder(this.lastOrder)
                .holiday(this.holiday)
                .capacity(this.capacity)
                .address(this.address)
                .build();
    }

}
