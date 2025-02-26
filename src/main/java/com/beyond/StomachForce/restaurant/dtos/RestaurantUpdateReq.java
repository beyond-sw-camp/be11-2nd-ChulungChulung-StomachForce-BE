package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.RestaurantAddress;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantUpdateReq {
    private String name;
    private String email;
    private String registrationNumber;  // 사업자덩록번호
    private String phoneNumber;
    private String description;
    private String addressCity;
    private String addressStreet;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LocalTime breakTimeStart;        // 브레이크 타임 시작
    private LocalTime breakTimeEnd;          // 브레이크 타임 끗
    private LocalTime lastOrder;
    private LocalDate holiday;
    private int capacity;
    private RestaurantAddress address;

    private String depositAvailable;
    private Long deposit;

    private RestaurantType restaurantType; // 매장 타입
    private String infoText;                // info는 그냥 뭐랄까 생성할 때 안만들고 나중에 수정할 때 만들 수 있도록 했습니다.

//    private List<MultipartFile> restaurantPhotos; // 새로 추가할 사진
//    private List<String> photoUrlsToRemove;  // 삭제할 사진 url 리스트 추가



}
