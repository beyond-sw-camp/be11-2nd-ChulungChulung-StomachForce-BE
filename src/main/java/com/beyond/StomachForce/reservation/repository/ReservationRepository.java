package com.beyond.StomachForce.reservation.repository;


import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
//    동시성이슈해결
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT SUM(r.peopleNumber) FROM Reservation r WHERE r.restaurant = :restaurant AND r.reservationTime BETWEEN :startTime AND :endTime")
    Integer sumPeopleNumberByRestaurantAndReservationTimeBetween(Restaurant restaurant, LocalTime startTime, LocalTime endTime);
}
