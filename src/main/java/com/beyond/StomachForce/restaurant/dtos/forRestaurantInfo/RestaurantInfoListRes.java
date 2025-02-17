package com.beyond.StomachForce.restaurant.dtos;


import com.beyond.StomachForce.restaurant.domain.select.RestaurantInfoStatus;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RestaurantInfoListRes {
    private Long id;
    private String informationText;
    private RestaurantInfoStatus status;


    public RestaurantInfoListRes(Long id, String informationText, RestaurantInfoStatus restaurantInfoStatus, LocalDateTime createdTime) {
    }
}
