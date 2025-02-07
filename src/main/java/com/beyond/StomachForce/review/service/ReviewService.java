package com.beyond.StomachForce.review.service;

import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.review.repository.ReviewPhotoRepository;
import com.beyond.StomachForce.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    public ReviewService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, ReviewPhotoRepository reviewPhotoRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.reviewPhotoRepository = reviewPhotoRepository;
    }

//    private String saveReview(ReviewSaveReq reviewSaveReq) {
//
//        if(reviewSaveReq.getContents().length()<10){
//            throw new IllegalArgumentException("리뷰 성의 없이 쓰지 마세요.");
//        }
//
//롱그인 하는 동안 잠깐 주석
//        Review review = reviewRepository.save(reviewSaveReq));
//    }
}
