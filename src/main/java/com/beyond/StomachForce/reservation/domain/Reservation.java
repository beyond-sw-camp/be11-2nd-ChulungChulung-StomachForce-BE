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
    private LocalDateTime reservationTime;
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
    @OneToMany(mappedBy = "reservation")
    private List<Menu> menuList;

//    id ,userId, restaurantId, reservationType, reservationDate, peopleNumber, method, mileage


    public void updateReservation(LocalDateTime reservationDateTime, Integer peopleNumber, Payment paymentMethod, Integer mileage) {
        this.reservationDate = reservationDateTime.toLocalDate();
        this.reservationTime = reservationDateTime;
        this.peopleNumber = peopleNumber;
        this.paymentMethod = paymentMethod != null ? paymentMethod : this.paymentMethod;
        this.mileage = mileage != null ? mileage : this.mileage;
    }
}
