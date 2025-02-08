package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class RestaurantDeleteReqDto {
    private Long id;
    private String restaurantEmail;
    private String restaurantStatus;

}
