package com.beyond.StomachForce.coupon.domain;


//import com.beyond.StomachForce.reservationDetail.domain.ReservationDetail;
import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@ToString
public class Coupon extends BaseReservationTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String couponCode;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponType couponType = CouponType.WON;
    private String couponIssue;
    @Column(nullable = false)
    private Integer discountAmount;
    private LocalDateTime dueDate;
    private String couponName;
//    @OneToMany(mappedBy = "coupon")
//    private ReservationDetail reservationDetail;
}
