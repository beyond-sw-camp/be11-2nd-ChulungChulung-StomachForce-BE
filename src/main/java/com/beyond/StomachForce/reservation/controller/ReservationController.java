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

    @PostMapping("/{id}/create")// 예약생성//id는 resturant의 id
    public String authorCreate(@RequestBody ReservationCreateReq reservationCreateReq, @PathVariable Long id) {
        reservationService.save(reservationCreateReq, id);
        return "OK";
    }

    @GetMapping("/{userId}/list") //예약한 유저의 예약 확인
    public ResponseEntity<?> myReservation(@PathVariable Long userId){
        List<ReservationListRes> reservationListRes = reservationService.myReservation(userId);
        return new ResponseEntity<>(reservationListRes, HttpStatus.OK);
    }
    @GetMapping("/{reservationId}/detail") //예약상세
    public ResponseEntity<?> reservationDetail(@PathVariable Long reservationId){
        ReservationDetailRes reservationDetailRes = reservationService.reservationDetail(reservationId);
        return new ResponseEntity<>(reservationDetailRes, HttpStatus.OK);
    }
}
