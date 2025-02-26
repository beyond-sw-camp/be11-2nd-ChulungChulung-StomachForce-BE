package com.beyond.StomachForce.reservation.service;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.coupon.domain.Coupon;
import com.beyond.StomachForce.coupon.repository.CouponRepository;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.menu.dto.MenuOrderReq;
import com.beyond.StomachForce.reservation.domain.Payment;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.reservation.domain.ReservationMenu;
import com.beyond.StomachForce.reservation.dtos.ReservationCreateReq;
import com.beyond.StomachForce.reservation.dtos.ReservationDetailRes;
import com.beyond.StomachForce.reservation.dtos.ReservationListRes;
import com.beyond.StomachForce.reservation.dtos.ReservationMenuRes;
import com.beyond.StomachForce.reservation.repository.ReservationRepository;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void save(ReservationCreateReq dto, Long restaurantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByIdentify(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("로그인되지 않은 사용자입니다. 로그인을 해주세요."));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("예약할 레스토랑을 찾을 수 없습니다."));
        System.out.println("📌 [DEBUG] 받은 예약 데이터: 날짜=" + dto.getReservationDate() + ", 시간=" + dto.getReservationTime());
        Map<Long, Integer> menuQuantityMap = dto.getMenus().stream()
                .collect(Collectors.toMap(MenuOrderReq::getMenuId, MenuOrderReq::getQuantity));
        // ✅ reservationDateTime이 null이면 오류 발생 방지
        if (dto.getReservationDate() == null || dto.getReservationTime()==null) {
            throw new IllegalStateException("예약할 날짜 및 시간이 올바르게 설정되지 않았습니다.");
        }

        // ✅ 날짜와 시간 분리

        // ✅ 라스트 오더 체크 (String → LocalTime 변환 후 비교)
        if (restaurant.getLastOrder() != null) {
            System.out.println("📌 라스트 오더 시간: " + restaurant.getLastOrder());
            System.out.println("📌 예약 시간: " + dto.getReservationTime());

            if (dto.getReservationTime().isAfter(restaurant.getLastOrder())) {
                throw new IllegalStateException("라스트 오더 이후에는 예약이 불가능합니다.");
            }
        } else {
            System.out.println("⚠️ 라스트 오더 시간이 설정되지 않음 (비교 스킵)");

        }
        // ✅ 영업시간 체크
        if (restaurant.getOpeningTime().isAfter(dto.getReservationTime())) {
            throw new IllegalStateException("레스토랑 오픈 전에는 예약이 불가능합니다.");
        }

        // ✅ 휴무일 체크
        if (restaurant.getHoliday() != null && restaurant.getHoliday().equals(dto.getReservationDate())) {
            throw new IllegalStateException("해당 날짜는 휴무일입니다.");
        }

        // ✅ 브레이크 타임 체크
        if (restaurant.getBreakTimeStart() != null && restaurant.getBreakTimeEnd() != null) {
            if (!dto.getReservationTime().isBefore(restaurant.getBreakTimeStart()) &&
                    !dto.getReservationTime().equals(restaurant.getBreakTimeEnd()) &&
                    !dto.getReservationTime().isAfter(restaurant.getBreakTimeEnd())) {
                throw new IllegalStateException("브레이크 타임 동안에는 예약이 불가능합니다.");
            }
        }

        // ✅ 예약 인원 초과 체크
        LocalTime startTime = dto.getReservationTime().withMinute(0).withSecond(0);
        LocalTime endTime = startTime.plusHours(1);

        Integer currentReservationsPeopleNumber = reservationRepository.sumPeopleNumberByRestaurantAndReservationTimeBetween(restaurant, dto.getReservationDate(),startTime, endTime);
        if (currentReservationsPeopleNumber == null) {
            currentReservationsPeopleNumber = 0;  // 예약된 인원이 없으면 0으로 설정
        }
        System.out.println("현재 예약인원 :" + currentReservationsPeopleNumber );
        if (currentReservationsPeopleNumber + dto.getPeopleNumber() > restaurant.getCapacity()) {
            throw new IllegalStateException("이 시간대에는 최대 인원을 초과하여 예약할 수 없습니다.");
        }

        // ✅ 예약 저장 (쿠폰 적용 여부 확인)
        Coupon coupon = null;
        if (dto.getCouponCode() != null && !dto.getCouponCode().isEmpty()) {
            coupon = couponRepository.findByCouponCode(dto.getCouponCode())
                    .orElseThrow(() -> new EntityNotFoundException("쿠폰을 찾을 수 없습니다."));
        }

        // ✅ 메뉴 리스트 조회
        List<Menu> menus = restaurant.getMenus().stream()
                .filter(menu -> menuQuantityMap.containsKey(menu.getId()))
                .peek(menu -> menu.setQuantity(menuQuantityMap.get(menu.getId()))) // ✅ 수량 설정
                .collect(Collectors.toList());
        // 🚨 메뉴를 하나도 찾지 못했다면 예외 발생 방지
        if (menus.isEmpty()) {
            throw new EntityNotFoundException("예약하려는 메뉴가 레스토랑의 메뉴 목록에 없습니다.");
        }

        System.out.println("📌 [DEBUG] 프론트에서 보낸 메뉴 목록:");
        dto.getMenus().forEach(menuOrderReq ->
                System.out.println("  - 메뉴 ID: " + menuOrderReq.getMenuId() + ", 수량: " + menuOrderReq.getQuantity())
        );
        System.out.println("📌 [DEBUG] 해당 레스토랑의 메뉴 목록:");
        restaurant.getMenus().forEach(menu ->
                System.out.println("  - 메뉴 ID: " + menu.getId() + ", 이름: " + menu.getName())
        );
        // ✅ 먼저 예약 객체 생성 (menus 없이)
        Reservation reservation = Reservation.builder()
                .peopleNumber(dto.getPeopleNumber())
                .paymentMethod(dto.getPayment() != null ? dto.getPayment() : Payment.CARD)
                .reservationDate(dto.getReservationDate())
                .reservationTime(dto.getReservationTime())
                .mileage(dto.getMileage())
                .restaurant(restaurant)
                .user(user)
                .coupon(coupon)
                .build();
        for (Menu m : menus){

            System.out.println(m.getId());
            System.out.println(m.getQuantity());
        }

// ✅ `ReservationMenu` 엔티티 리스트 생성
        List<ReservationMenu> reservationMenus = menus.stream()
                .map(menu -> ReservationMenu.builder()
                        .reservation(reservation)
                        .menu(menu)
                        .quantity(menuQuantityMap.get(menu.getId()))  // ✅ 수량 저장
                        .build())
                .collect(Collectors.toList());

// ✅ 예약에 메뉴 추가 (한 번에 리스트 추가 가능)
        reservation.addReservationMenu(reservationMenus);

// ✅ 최종적으로 예약 저장
        reservationRepository.save(reservation);
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
                    .restaurantId(r.getRestaurant().getId())
                    .build();

            reservationListRes.add(reservationListRes1);
        }
        return reservationListRes;
    }
    public void deleteReservation(Long reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다."));

        // 현재 사용자가 본인의 예약만 삭제할 수 있도록 체크
        if (!reservation.getUser().equals(user)) {
            throw new IllegalStateException("본인의 예약만 삭제할 수 있습니다.");
        }

        // 예약 삭제
        reservationRepository.delete(reservation);
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
        // ✅ 주문한 메뉴 리스트 변환
        List<ReservationMenuRes> orderedMenus = reservation.getReservationMenus().stream()
                .map(reservationMenu -> ReservationMenuRes.builder()
                        .name(reservationMenu.getMenu().getName())
                        .imageUrl(reservationMenu.getMenu().getMenuPhoto())
                        .quantity(reservationMenu.getQuantity()) // ✅ 주문 수량 포함
                        .price(reservationMenu.getMenu().getPrice())
                        .build())
                .collect(Collectors.toList());

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
                    .restaurantAddress(reservation.getRestaurant().getAddress().getFullAddress())
                    .restaurantNumber(reservation.getRestaurant().getPhoneNumber())
                    .paymentMethod(reservation.getPaymentMethod().toString())
                    .reservationStatus(reservation.getStatus().toString())
                    .useMilege(reservation.getMileage())
                    .orderedMenus(orderedMenus) // ✅ 주문한 메뉴 추가
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
                    .reservationPeopleNumber(reservation.getPeopleNumber())
                    .restaurantAddress(reservation.getRestaurant().getAddress().getFullAddress())
                    .restaurantNumber(reservation.getRestaurant().getPhoneNumber())
                    .paymentMethod(reservation.getPaymentMethod().toString())
                    .reservationStatus(reservation.getStatus().toString())
                    .useMilege(reservation.getMileage())
                    .couponName(reservation.getCoupon().getCouponName())
                    .discountAmount(reservation.getCoupon().getDiscountAmount())
                    .couponType(reservation.getCoupon().getCouponType())
                    .orderedMenus(orderedMenus) // ✅ 주문한 메뉴 추가
                    .build();
            //예약번호,예약일자,예약자,예약입금현황,가게이름,가게연락처,가게주소,결제방법,사용한마일리지, 사용한쿠폰
            return reservationDetailRes;
        }

    }
}
