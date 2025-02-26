package com.beyond.StomachForce.restaurant.dtos;

import com.beyond.StomachForce.restaurant.domain.select.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantSearchDto {
    private String name;
    private String address;
    private String restaurantType;

}
