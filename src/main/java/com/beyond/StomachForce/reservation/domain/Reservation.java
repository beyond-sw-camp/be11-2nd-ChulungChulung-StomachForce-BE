package com.beyond.StomachForce.reservation.domain;


import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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
    private LocalDateTime reservationDate;
    private Integer peopleNumber;
    @Enumerated
    @Builder.Default
    private Payment paymentMethod = Payment.CARD;
    @Enumerated
    @Builder.Default
    private Status status = Status.N; //예약금 납부 여부
    private Integer mileage;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

//    id ,userId, restaurantId, reservationType, reservationDate, peopleNumber, method, mileage
}
