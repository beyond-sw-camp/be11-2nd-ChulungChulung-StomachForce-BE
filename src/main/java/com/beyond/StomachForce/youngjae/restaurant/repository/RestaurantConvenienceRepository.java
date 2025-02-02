package com.beyond.StomachForce.youngjae.restaurant.repository;

import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantConvenience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface RestaurantConvenienceRepository extends JpaRepository<RestaurantConvenience, Long> {
}
