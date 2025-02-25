package com.beyond.StomachForce.reservation.dtos;


import com.beyond.StomachForce.restaurant.domain.Restaurant;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationListRes {
    private Long id;
    private String restaurantName;
    private Long restaurantId;
//    private Integer totalCount; // 나중에 menu추가되면 연결 예정.

}
