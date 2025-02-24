package com.beyond.StomachForce.reservation.dtos;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.reservation.domain.Payment;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationCreateReq {
    private LocalDateTime reservationDateTime;
    private Integer peopleNumber;
    private Payment payment;
    private Integer mileage;
    private String couponCode;
    private List<Menu> menuList;

    //menu domain 추가되면 메뉴까지 추가 예정.

    public Reservation toEntity(User user, Coupon coupon, Restaurant restaurant){
        return Reservation.builder()
                .peopleNumber(this.peopleNumber)
                .paymentMethod(this.payment != null ? this.payment : Payment.CARD)
                .reservationDate(reservationDateTime.toLocalDate())  // 날짜만 저장
                .reservationTime(reservationDateTime)
                .mileage(this.mileage)
                .menuList(this.menuList)
                .restaurant(restaurant)
                .user(user)
                .coupon(coupon)
                .build();
    }
    public Reservation toEntity(User user, Restaurant restaurant){
        return Reservation.builder()
                .peopleNumber(this.peopleNumber)
                .paymentMethod(this.payment != null ? this.payment : Payment.CARD)
                .reservationDate(reservationDateTime.toLocalDate())  // 날짜만 저장
                .reservationTime(reservationDateTime)
                .mileage(this.mileage)
                .menuList(this.menuList)
                .restaurant(restaurant)
                .user(user)
                .build();
    }


}
