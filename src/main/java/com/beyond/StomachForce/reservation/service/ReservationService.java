package com.beyond.StomachForce.reservation.service;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.coupon.repository.CouponRepository;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.reservation.dtos.ReservationCreateReq;
import com.beyond.StomachForce.reservation.dtos.ReservationDetailRes;
import com.beyond.StomachForce.reservation.dtos.ReservationListRes;
import com.beyond.StomachForce.reservation.repository.ReservationRepository;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final RestaurantRepository restaurantRepository;

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, CouponRepository couponRepository, RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void save(ReservationCreateReq dto, Long restaurantId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (dto.getCouponCode()==null||dto.getCouponCode().isEmpty()){
            User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()->new EntityNotFoundException("user is not found"));
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new EntityNotFoundException("restaurant is not found"));
            reservationRepository.save(dto.toEntity(user, restaurant));
        }else {
            User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()->new EntityNotFoundException("user is not found"));
            Coupon coupon = couponRepository.findByCouponCode(dto.getCouponCode()).orElseThrow(()->new EntityNotFoundException("coupon is not found"));
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new EntityNotFoundException("restaurant is not found"));
            reservationRepository.save(dto.toEntity(user, coupon, restaurant));
        }

    }

    public List<ReservationListRes> myReservation(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()->new EntityNotFoundException("user is not found"));
        List<Reservation> reservations = user.getReservationList();
        List<ReservationListRes> reservationListRes = new ArrayList<>();
        for (Reservation r : reservations){
            ReservationListRes reservationListRes1 = ReservationListRes.builder()
                    .id(r.getId())
                    .restaurantName(r.getRestaurant().getName())
                    .build();

            reservationListRes.add(reservationListRes1);
        }
        return reservationListRes;
    }
    public ReservationDetailRes reservationDetail(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()->new EntityNotFoundException("user is not found"));
        List<Reservation> reservations = user.getReservationList();
        Reservation reservation = new Reservation();
        for (Reservation r : reservations){
            if (r.getId()==id){
                reservation=r;
            }
        }
        if (reservation.getCoupon()==null){
            ReservationDetailRes reservationDetailRes = ReservationDetailRes.builder()
                    .id(reservation.getId())
                    .restaurantName(reservation.getRestaurant().getName())
                    .reservationDate(reservation.getReservationDate())
                    .userName(reservation.getUser().getName())
                    .reservationStatus(reservation.getStatus().toString())
                    .restaurantAddress(reservation.getRestaurant().getAddress().getStreet())
                    .restaurantNumber(reservation.getRestaurant().getPhoneNumber())
                    .paymentMethod(reservation.getPaymentMethod().toString())
                    .reservationStatus(reservation.getStatus().toString())
                    .useMilege(reservation.getMileage())
                    .build();
            return reservationDetailRes;
        }else{
            ReservationDetailRes reservationDetailRes = ReservationDetailRes.builder()
                    .id(reservation.getId())
                    .reservationDate(reservation.getReservationDate())
                    .userName(reservation.getUser().getName())
                    .restaurantName(reservation.getRestaurant().getName())
                    .reservationStatus(reservation.getStatus().toString())
                    .restaurantAddress(reservation.getRestaurant().getAddress().getStreet())
                    .restaurantNumber(reservation.getRestaurant().getPhoneNumber())
                    .paymentMethod(reservation.getPaymentMethod().toString())
                    .reservationStatus(reservation.getStatus().toString())
                    .useMilege(reservation.getMileage())
                    .couponName(reservation.getCoupon().getCouponName())
                    .build();
            //예약번호,예약일자,예약자,예약입금현황,가게이름,가게연락처,가게주소,결제방법,사용한마일리지, 사용한쿠폰
            return reservationDetailRes;
        }

    }
}
