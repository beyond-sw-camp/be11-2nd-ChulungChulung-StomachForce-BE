package com.beyond.StomachForce.reservation.dtos;


import com.beyond.StomachForce.coupon.domain.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationDetailRes {
    //예약번호,예약일자,예약자,예약입금현황,가게이름,가게연락처,가게주소,결제방법,사용한마일리지, 사용한쿠폰

    private Long id;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private String userName;
    private String reservationStatus;
    private String restaurantName;
    private String restaurantNumber;
    private String restaurantAddress;
    private String paymentMethod;
    private Integer useMilege;
    private String couponName;
    private CouponType couponType;
    private Integer discountAmount;
    private Integer reservationPeopleNumber;

    // ✅ 주문한 메뉴 정보 추가
    private List<ReservationMenuRes> orderedMenus;
}
