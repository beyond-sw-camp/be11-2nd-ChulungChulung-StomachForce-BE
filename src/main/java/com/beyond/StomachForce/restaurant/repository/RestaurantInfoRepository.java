package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.restaurant.domain.RestaurantInfo;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantInfoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RestaurantInfoRepository extends JpaRepository<RestaurantInfo,Long> {
    List<RestaurantInfo> findByRestaurantIdAndRestaurantInfoStatusOrderByCreatedTimeDesc(Long restaurantId, RestaurantInfoStatus status);
    List<RestaurantInfo> findByRestaurantIdOrderByCreatedTimeDesc(Long restaurantId);
    Optional<RestaurantInfo> findFirstByRestaurantIdAndRestaurantInfoStatusOrderByCreatedTimeDesc(Long restaurantId, RestaurantInfoStatus status);
}
