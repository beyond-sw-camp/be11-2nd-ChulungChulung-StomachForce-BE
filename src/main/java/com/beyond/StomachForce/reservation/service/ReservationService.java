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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        //1. 로그인 했는지 확인하기.
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()-> new EntityNotFoundException("로그인 되지않은 사용자입니다. 로그인을 해주세요."));
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()-> new EntityNotFoundException("예약할 레스토랑을 찾아서주세요."));
        LocalDateTime reservationTime = dto.getReservationTime();
        LocalDate reservationDate = dto.getReservationDate();
        int reservationHour = reservationTime.getHour();

        // 1. 휴무일 체크하기.
        if (restaurant.getHoliday() != null && restaurant.getHoliday().equals(reservationDate)) {
            throw new IllegalStateException("예약할 수 없는 날입니다. (휴무일)");
        }
//        영업시간 전에 예약 제한하기
        if (restaurant.getOpeningTime().isAfter(reservationTime)) {
            throw new IllegalStateException("레스토랑 오픈 전에는 예약이 불가능합니다.");
        }
        // 2. 라스트 오더 이후 예약 제한하기.
        if (restaurant.getLastOrder().isBefore(reservationTime)) {
            throw new IllegalStateException("라스트 오더 이후에는 예약이 불가능합니다.");
        }

        // 3. 브레이크 타임 체크하기.
        if (restaurant.getBreakTimeStart() != null && restaurant.getBreakTimeEnd() != null) {
            if (!reservationTime.isBefore(restaurant.getBreakTimeStart()) && !reservationTime.isAfter(restaurant.getBreakTimeEnd())) {
                throw new IllegalStateException("브레이크 타임 동안에는 예약이 불가능합니다.");
            }
        }
        // 4. 한 타임(정각 기준 1시간) 예약 인원 초과 체크
        LocalDateTime startTime = reservationDate.atTime(reservationTime.getHour(), reservationTime.getMinute());
        LocalDateTime endTime = startTime.plusHours(1);

        Integer currentReservationsPeopleNumber = reservationRepository.sumPeopleNumberByRestaurantAndReservationTimeBetween(restaurant, startTime, endTime);
        if (currentReservationsPeopleNumber == null) {
            currentReservationsPeopleNumber = 0;  // 예약된 인원이 없으면 0으로 설정
        }
        if (currentReservationsPeopleNumber + dto.getPeopleNumber() > restaurant.getCapacity()) {
            throw new IllegalStateException("이 시간대에는 최대 인원을 초과하여 예약할 수 없습니다.");
        }
        // 5. 예약 저장 (쿠폰 적용 여부 확인)
        if (dto.getCouponCode() == null || dto.getCouponCode().isEmpty()) {
            reservationRepository.save(dto.toEntity(user, restaurant));
        } else {
            Coupon coupon = couponRepository.findByCouponCode(dto.getCouponCode())
                    .orElseThrow(() -> new EntityNotFoundException("coupon is not found"));
            reservationRepository.save(dto.toEntity(user, coupon, restaurant));
        }


//        if (dto.getCouponCode()==null||dto.getCouponCode().isEmpty()){
//            User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()->new EntityNotFoundException("user is not found"));
//            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new EntityNotFoundException("restaurant is not found"));
//            reservationRepository.save(dto.toEntity(user, restaurant));
//        }else {
//            User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()->new EntityNotFoundException("user is not found"));
//            Coupon coupon = couponRepository.findByCouponCode(dto.getCouponCode()).orElseThrow(()->new EntityNotFoundException("coupon is not found"));
//            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new EntityNotFoundException("restaurant is not found"));
//            reservationRepository.save(dto.toEntity(user, coupon, restaurant));
//        }

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
                    .reservationTime(reservation.getReservationTime())
                    .reservationPeopleNumber(reservation.getPeopleNumber())
                    .reservationPeopleNumber(reservation.getPeopleNumber())
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
                    .reservationTime(reservation.getReservationTime())
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
