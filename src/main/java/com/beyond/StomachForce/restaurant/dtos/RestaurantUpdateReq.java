package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.RestaurantAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantUpdateReq {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String description;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private LocalDateTime lastOrder;
    private LocalDate holiday;
    private int capacity;
    private RestaurantAddress address;

}
