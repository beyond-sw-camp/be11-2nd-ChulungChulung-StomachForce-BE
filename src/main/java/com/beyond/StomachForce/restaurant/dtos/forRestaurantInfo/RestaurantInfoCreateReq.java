package com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo;

import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.RestaurantInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantInfoCreateReq {
    private String infoText;


    public RestaurantInfo toEntity(Restaurant restaurant) {
        return RestaurantInfo.builder()
                .restaurant(restaurant)
                .informationText(this.infoText)
                .build();
    }
}

