package com.beyond.StomachForce.reservation.repository;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
//    동시성이슈해결
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT SUM(r.peopleNumber) FROM Reservation r " +
            "WHERE r.restaurant = :restaurant " +
            "AND r.reservationDate = :reservationDate " + // ✅ 날짜 추가!
            "AND r.reservationTime BETWEEN :startTime AND :endTime")
    Integer sumPeopleNumberByRestaurantAndReservationTimeBetween(
            Restaurant restaurant,
            LocalDate reservationDate, // ✅ 날짜 추가
            LocalTime startTime,
            LocalTime endTime
    );



    // 예약시간 이후에만 리뷰 남기게 하겠읍니다
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.user = :user " +
            "AND r.restaurant = :restaurant " +
            "AND (r.reservationDate < CURRENT_DATE " +
            "OR (r.reservationDate = CURRENT_DATE AND r.reservationTime < CURRENT_TIME))")
    List<Reservation> findCompletedReservations(User user, Restaurant restaurant);
}
