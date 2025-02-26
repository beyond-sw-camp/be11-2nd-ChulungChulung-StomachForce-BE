package com.beyond.StomachForce.reservation.domain;


import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Reservation extends BaseReservationTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer peopleNumber;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Payment paymentMethod = Payment.CARD;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.N; //예약금 납부 여부
    private Integer mileage;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    // ✅ 연결 테이블로 변경하여 다대다 문제 해결
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationMenu> reservationMenus = new ArrayList<>();

    // ✅ 예약에 메뉴 리스트 추가하는 메서드
    public void addReservationMenu(List<ReservationMenu> reservationMenuList) {
        if (this.reservationMenus == null) {  // ✅ NullPointerException 방지
            this.reservationMenus = new ArrayList<>();
        }
        for (ReservationMenu reservationMenu : reservationMenuList) {
            this.reservationMenus.add(reservationMenu);
            reservationMenu.setReservation(this);
        }
    }


    public void updateReservation(LocalDate reservationDate,LocalTime reservationTime, Integer peopleNumber, Payment paymentMethod, Integer mileage) {
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.peopleNumber = peopleNumber;
        this.paymentMethod = paymentMethod != null ? paymentMethod : this.paymentMethod;
        this.mileage = mileage != null ? mileage : this.mileage;
    }
}
