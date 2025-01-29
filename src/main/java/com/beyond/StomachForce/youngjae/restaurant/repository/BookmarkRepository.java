package com.beyond.StomachForce.youngjae.restaurant.repository;

import com.beyond.StomachForce.youngjae.restaurant.entity.Bookmark;
import com.beyond.StomachForce.youngjae.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 나를 즐찾한 사람들 수 구하기
    @Query("select count(b) from Bookmark b where b.restaurant.id = :restaurantId")
    Long countByRestaurantId(@Param("restaurantId") Long restaurantId);
}
