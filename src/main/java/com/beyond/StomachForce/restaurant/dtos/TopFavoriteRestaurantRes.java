package com.beyond.StomachForce.restaurant.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class TopFavoriteRestaurantRes {

    private Long restaurantId;
    private String restaurantImage;
    private String restaurantName;
    private Double rating;
}
