package com.beyond.StomachForce.reservation.controller;


import com.beyond.StomachForce.reservation.dtos.ReservationCreateReq;
import com.beyond.StomachForce.reservation.dtos.ReservationDetailRes;
import com.beyond.StomachForce.reservation.dtos.ReservationListRes;
import com.beyond.StomachForce.reservation.service.ReservationService;
import com.beyond.StomachForce.restaurant.dtos.RestaurantCreateReq;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{id}/create")// 회원가입
    public String authorCreate(@RequestBody ReservationCreateReq reservationCreateReq, @PathVariable Long id) {
        System.out.println("📌 [DEBUG] 백엔드에서 받은 JSON 데이터: " + reservationCreateReq);
        reservationService.save(reservationCreateReq, id);
        return "OK";
    }

    @GetMapping("/{userId}/list")
    public ResponseEntity<?> myReservation(@PathVariable Long userId){
        List<ReservationListRes> reservationListRes = reservationService.myReservation(userId);
        return new ResponseEntity<>(reservationListRes, HttpStatus.OK);
    }
    @GetMapping("/{reservationId}/detail")
    public ResponseEntity<?> reservationDetail(@PathVariable Long reservationId){
        ReservationDetailRes reservationDetailRes = reservationService.reservationDetail(reservationId);
        return new ResponseEntity<>(reservationDetailRes, HttpStatus.OK);
    }
    @DeleteMapping("/{reservationId}/delete") // 예약 삭제
    public ResponseEntity<?> deleteReservation(@PathVariable Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return new ResponseEntity<>("예약이 성공적으로 삭제되었습니다.", HttpStatus.OK);
    }
}
