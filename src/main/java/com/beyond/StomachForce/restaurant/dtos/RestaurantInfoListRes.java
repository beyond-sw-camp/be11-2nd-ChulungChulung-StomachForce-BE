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


    //      인텔리제이랑 지피티가 둘 다 만들으래요...dto에서 생성자 만드는게 불변성을 보장하기때문이립니다.
    public RestaurantInfoListRes(Long id, String informationText, RestaurantInfoStatus restaurantInfoStatus, LocalDateTime createdTime) {
    }
}
