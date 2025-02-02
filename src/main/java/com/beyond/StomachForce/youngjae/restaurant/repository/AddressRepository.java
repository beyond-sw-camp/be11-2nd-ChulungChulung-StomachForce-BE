package com.beyond.StomachForce.youngjae.restaurant.repository;

import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<RestaurantAddress, Long> {
}
