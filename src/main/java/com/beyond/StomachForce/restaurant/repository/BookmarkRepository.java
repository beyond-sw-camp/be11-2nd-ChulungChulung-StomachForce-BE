package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.restaurant.domain.Bookmark;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 나를 즐찾한 사람들 수 구하기
    @Query("select count(b) from Bookmark b where b.restaurant.id = :restaurantId")
    Long countByRestaurantId(@Param("restaurantId") Long restaurantId);

    Page<Bookmark> findByUserAndRestaurant_RestaurantStatus(User user, RestaurantStatus restaurantStatus, Pageable pageable);
    void deleteByUserAndRestaurant(User user, Restaurant restaurant);
    Optional<Bookmark> findByUserAndRestaurant(User user, Restaurant restaurant);
}
