package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.RestaurantPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantPhotoRepository extends JpaRepository<RestaurantPhoto, Long> {
    Optional<List<RestaurantPhoto>> findByRestaurant(Restaurant restaurant);
    RestaurantPhoto findByPhotoUrl(String photoUrl);
    int deleteAllByRestaurant(Restaurant restaurant);
}
