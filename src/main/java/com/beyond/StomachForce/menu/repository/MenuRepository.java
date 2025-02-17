package com.beyond.StomachForce.menu.repository;

import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    List<Menu> findByRestaurant(Restaurant restaurant);
}
