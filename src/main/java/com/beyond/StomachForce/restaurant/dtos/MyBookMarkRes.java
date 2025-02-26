package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MyBookMarkRes {
    private Long restaurantId;
    private String restaurantName;
    private String restaurantPhoto;
}
