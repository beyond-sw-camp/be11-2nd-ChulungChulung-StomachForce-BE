package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestaurantListRes {
    private Long id;                    //아묻따
    private String name;                //레스토랑명
    private Double averageRating;       // 레스토랑 평균 별정(소수로 나옵니다)
    private Long bookmarkCount;         // 즐찾한 사람들 수
    private String address;             // 주소
    private int reviewCount;            // 리뷰 개수
    private String imagePath;           // 이미지 사진(항상 0번째 사진 표출)
    private String restaurantType;      // 한,중,일,양식,퓨젼
    private List<String> convenience;   // 편의사항

}
