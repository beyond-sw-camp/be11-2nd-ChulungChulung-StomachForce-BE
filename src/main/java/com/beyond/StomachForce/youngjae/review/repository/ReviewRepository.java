package com.beyond.StomachForce.youngjae.review.repository;

import com.beyond.StomachForce.youngjae.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    //      내 레스토랑(내 업장)의 리뷰 조회
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId ORDER BY r.createdTime DESC")
    List<Review> findByRestaurantId(@Param("restaurantId") Long restaurantId);


    //      레스토랑 평귱별점 조회
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double findAverageRatingByRestaurantId(@Param("restaurantId") Long restaurantId);
}
