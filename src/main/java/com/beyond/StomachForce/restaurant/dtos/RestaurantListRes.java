package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantListRes {
    private Long id;                    //아묻따
    private String name;                //레스토랑명
    private Double averageRating;       // 레스토랑 평균 별정(소수로 나옵니다)
    private Long bookmarkCount;         // 즐찾한 사람들 수
    private String address;

}
