package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.restaurant.domain.RestaurantPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantPhotoRepository extends JpaRepository<RestaurantPhoto, Long> {
}
