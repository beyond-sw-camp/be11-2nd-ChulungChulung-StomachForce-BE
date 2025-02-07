package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.restaurant.entity.RestaurantAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<RestaurantAddress, Long> {
}
