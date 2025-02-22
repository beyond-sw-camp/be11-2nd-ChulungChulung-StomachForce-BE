package com.beyond.StomachForce.review.repository;

import com.beyond.StomachForce.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantId(Long restaurantId);
    Optional<Review> findByIdAndRestaurantId(Long id, Long restaurantId);
}