package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.RestaurantAddress;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantUpdateReq {
    private String name;
    private String email;
    @Size(min = 8)
    private String password;
    private String currentPassword;     //현재 비밀번호랑 확인 후에 바꿀 수 있도록 하기 위해서 넣었읍니다.
    private String phoneNumber;
    private String description;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private LocalDateTime lastOrder;
    private LocalDate holiday;
    private int capacity;
    private RestaurantAddress address;

    private MultipartFile restaurantPhotos;

}
