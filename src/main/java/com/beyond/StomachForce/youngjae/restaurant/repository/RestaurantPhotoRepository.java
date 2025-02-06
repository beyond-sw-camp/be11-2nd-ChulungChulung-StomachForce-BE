package com.beyond.StomachForce.youngjae.restaurant.repository;

import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantPhotoRepository extends JpaRepository<RestaurantPhoto, Long> {
}
