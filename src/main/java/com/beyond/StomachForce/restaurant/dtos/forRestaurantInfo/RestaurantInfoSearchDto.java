package com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
//info paging 처리 할때 필요한 dto. 검색조건 넣을거임 ListRes는 결과를 담는 값이고 이건 조건을 위해서 두는 dto라 따로 만듦.
public class RestaurantInfoSearchDto {
    private Long restaurantId;
    private String infoText;
}
