package com.beyond.StomachForce.reservation.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationMenuRes {
    private String name;    // 메뉴 이름
    private String imageUrl; // 메뉴 이미지 URL
    private Integer quantity; // 주문 수량
    private Long price;  // 메뉴 가격
}
