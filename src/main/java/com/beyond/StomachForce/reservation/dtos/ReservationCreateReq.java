package com.beyond.StomachForce.reservation.dtos;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.menu.dto.MenuOrderReq;
import com.beyond.StomachForce.reservation.domain.Payment;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.reservation.domain.ReservationMenu;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<MenuOrderReq> menus;

    //menu domain 추가되면 메뉴까지 추가 예정.

    public Reservation toEntity(User user, Restaurant restaurant, List<Menu> menuEntities, Coupon coupon) {
        Reservation reservation = Reservation.builder()
                .peopleNumber(this.peopleNumber)
                .paymentMethod(this.payment != null ? this.payment : Payment.CARD)
                .reservationDate(this.reservationDate)
                .reservationTime(this.reservationTime)
                .mileage(this.mileage)
                .restaurant(restaurant)
                .user(user)
                .coupon(coupon)
                .build();
        List<ReservationMenu> reservationMenus = menus.stream()
                .map(menuOrderReq -> {
                    Menu menu = menuEntities.stream()
                            .filter(m -> m.getId().equals(menuOrderReq.getMenuId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));

                    return ReservationMenu.builder()
                            .reservation(reservation)
                            .menu(menu)
                            .quantity(menuOrderReq.getQuantity())  // ✅ 수량 설정
                            .build();
                })
                .collect(Collectors.toList());

        reservation.addReservationMenu(reservationMenus);
        return reservation;
    }
    public Reservation toEntity(User user, Restaurant restaurant, List<Menu> menuEntities) {
        Reservation reservation = Reservation.builder()
                .peopleNumber(this.peopleNumber)
                .paymentMethod(this.payment != null ? this.payment : Payment.CARD)
                .reservationDate(this.reservationDate)
                .reservationTime(this.reservationTime)
                .mileage(this.mileage)
                .restaurant(restaurant)
                .user(user)
                .build();

        List<ReservationMenu> reservationMenus = menus.stream()
                .map(menuOrderReq -> {
                    Menu menu = menuEntities.stream()
                            .filter(m -> m.getId().equals(menuOrderReq.getMenuId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));

                    return ReservationMenu.builder()
                            .reservation(reservation)
                            .menu(menu)
                            .quantity(menuOrderReq.getQuantity())  // ✅ 수량 설정
                            .build();
                })
                .collect(Collectors.toList());

        reservation.addReservationMenu(reservationMenus);
        return reservation;
    }


}
