package com.beyond.StomachForce.review.repository;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantId(Long restaurantId);
    List<Review> findByRestaurantIdOrderByCreatedTimeDesc(Long restaurantId);
    Optional<Review> findByIdAndRestaurantId(Long id, Long restaurantId);
    boolean existsByUserAndReservation(User user, Reservation reservation);

    // 리뷰 쓸 때 검증용으로 사용할 쿼리입니다~ 
    @Query("SELECT COUNT(r) > 0 FROM Review r " +
            "WHERE r.user = :user " +
            "AND r.restaurant = :restaurant")
    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);
}