package com.beyond.StomachForce.coupon.domain;


//import com.beyond.StomachForce.reservationDetail.domain.ReservationDetail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@ToString
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String couponCode;
    @Enumerated
    @Builder.Default
    private CouponType couponType = CouponType.WON;
    private String couponIssue;
    private Integer discountAmount;
    private LocalDateTime createdTime;
    private LocalDateTime dueDate;
    private String description;
//    @OneToMany(mappedBy = "coupon")
//    private ReservationDetail reservationDetail;
}
