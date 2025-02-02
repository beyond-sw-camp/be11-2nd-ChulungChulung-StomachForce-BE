package com.beyond.StomachForce.youngjae.restaurant.repository;

import com.beyond.StomachForce.youngjae.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {


    //      레스토랑 네임으로 검색하기 위함
    List<Restaurant> findByNameContaining(String restaurantName);

    //      예약 많은 순으로 정렬
    List<Restaurant> findByOrderByDepositDesc();

    //      즐찾많은 순으로 정렬(지선생)
    @Query("SELECT r FROM Restaurant r LEFT JOIN r.bookmarks b GROUP BY r ORDER BY COUNT(b) DESC")
    List<Restaurant> findAllOrderByBookmarkCountDesc();

    //      별점 높은 순으로 정렬(지선생)
    @Query("SELECT r FROM Restaurant r LEFT JOIN r.reviews rev GROUP BY r ORDER BY AVG(rev.rating.value) DESC")
    List<Restaurant> findAllOrderByRatingDesc();

    Optional<Restaurant> findByEmail(String email);

    Optional<Restaurant> findByRegistrationNumber(String registrationNumber);




}
