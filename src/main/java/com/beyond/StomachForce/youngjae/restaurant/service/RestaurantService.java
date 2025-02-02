package com.beyond.StomachForce.youngjae.restaurant.service;

import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantCreateReq;
import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantDetailRes;
import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantListRes;
import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantUpdateReq;
import com.beyond.StomachForce.youngjae.restaurant.entity.Bookmark;
import com.beyond.StomachForce.youngjae.restaurant.entity.Restaurant;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantPhoto;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.BookmarkType;
import com.beyond.StomachForce.youngjae.restaurant.repository.BookmarkRepository;
import com.beyond.StomachForce.youngjae.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.youngjae.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantService {

    @Autowired
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
//    private final PasswordEncoder passwordEncoder;
    private final BookmarkRepository bookmarkRepository;

// 로그인 의존성 보류에 따라 PasswordEncoder passwordEncoder,잠시 주석 처리함.
    public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,  BookmarkRepository bookmarkRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
//        this.passwordEncoder = passwordEncoder;
        this.bookmarkRepository = bookmarkRepository;
    }

    public List<RestaurantListRes> findAll(){
        return restaurantRepository.findAll().stream().map
                (r->r.listDtoFromEntity()).collect(Collectors.toList());
    }

    public void save(RestaurantCreateReq restaurantCreateReq){

        if(restaurantRepository.findByEmail(restaurantCreateReq.getEmail()).isPresent()){
            throw new IllegalArgumentException("email already exists");
        }
        if(restaurantCreateReq.getPassword().length()<8){
            throw new IllegalArgumentException("비번 너무 짧아요");
        }
//        Restaurant restaurant = restaurantRepository.save(restaurantCreateReq
//                .toEntity(passwordEncoder.encode(restaurantCreateReq.getPassword())));
    }

    public void update(String email, RestaurantUpdateReq restaurantUpdateReq){
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("email not found"));

        restaurant.updateProfile(restaurantUpdateReq);
        restaurantRepository.save(restaurant);
    }

    //사업자번호로 디테일 화면 확인
    public RestaurantDetailRes findByRegistrationNumber(String registrationNumber){
        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant with registration number " + registrationNumber + " not found"));
        return restaurant.detailFromEntity();
    }

    //이메일로 디테일 화면 확인
    public RestaurantDetailRes findByEmail (String email){
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant with email " + email + " not found"));
        return restaurant.detailFromEntity();
    }

    //id로 사진 찾는 메서드(레스토랑 아이디 활용)
    public List<String> findPhotosByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        return restaurant.getPhotos().stream().map(RestaurantPhoto::getPhotoUrl).collect(Collectors.toList());
    }

    //북마크 (토글)
    public void toggleBookmark(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Optional<Bookmark> bookmark = bookmarkRepository.findById(restaurantId);

        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get()); // 즐겨찾기 삭제
        } else {
            Bookmark newBookmark = Bookmark.builder().restaurant(restaurant).bookmarkType(BookmarkType.YES).build();
            bookmarkRepository.save(newBookmark); // 즐겨찾기 추가
        }
    }
}
