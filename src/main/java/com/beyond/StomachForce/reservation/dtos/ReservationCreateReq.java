package com.beyond.StomachForce.reservation.dtos;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.reservation.domain.Payment;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationCreateReq {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime reservationTime;
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
                .reservationDate(this.reservationDate)  // 날짜만 저장
                .reservationTime(this.reservationTime)
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
                .reservationDate(this.reservationDate)  // 날짜만 저장
                .reservationTime(this.reservationTime)
                .mileage(this.mileage)
                .menuList(this.menuList)
                .restaurant(restaurant)
                .user(user)
                .build();
    }


}
