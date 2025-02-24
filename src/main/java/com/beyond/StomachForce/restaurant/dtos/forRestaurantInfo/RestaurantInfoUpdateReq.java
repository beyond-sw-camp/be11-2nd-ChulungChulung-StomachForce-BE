package com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Getter

public class RestaurantInfoUpdateReq {
    private String informationText;
}
