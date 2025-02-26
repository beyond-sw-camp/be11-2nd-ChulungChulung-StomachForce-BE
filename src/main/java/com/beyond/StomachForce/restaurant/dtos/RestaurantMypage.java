package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantMypage {
    private Long id;
    private String name;
    private String email;
    private String description;
    private String phoneNumber;
    private String address;
    private String restaurantType;
}
