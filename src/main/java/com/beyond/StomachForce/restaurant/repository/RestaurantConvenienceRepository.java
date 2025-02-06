package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.restaurant.entity.RestaurantConvenience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantConvenienceRepository extends JpaRepository<RestaurantConvenience, Long> {
}
