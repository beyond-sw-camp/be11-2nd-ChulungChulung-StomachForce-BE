package com.beyond.StomachForce.restaurant.repository;

import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantType;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    @Query("SELECT r FROM Restaurant r LEFT JOIN r.reviews rev GROUP BY r.id ORDER BY AVG(rev.rating) DESC")
    List<Restaurant> findAllByOrderByRatingDesc();

    Optional<Restaurant> findByEmail(String email);

    Optional<Restaurant> findByRegistrationNumber(String registrationNumber);

    Optional<Restaurant> findByRegistrationNumberAndRestaurantStatus(String registrationNumber, Enum status);

    @Query("SELECT r FROM Restaurant r LEFT JOIN r.reviews rev GROUP BY r.id ORDER BY COALESCE(AVG(CAST(rev.rating AS integer)), 0) DESC")
    List<Restaurant> findTopRestaurantsByRating(Pageable pageable);

    Page<Restaurant> findAll(Specification<Restaurant> specification, Pageable pageable);

    Optional<Object> findByName(@NotEmpty String name);
}
