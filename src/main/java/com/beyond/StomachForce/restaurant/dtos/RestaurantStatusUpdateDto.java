package com.beyond.StomachForce.restaurant.dtos;


import com.beyond.StomachForce.restaurant.domain.select.RestaurantStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantStatusUpdateDto {
    private RestaurantStatus status;
}
