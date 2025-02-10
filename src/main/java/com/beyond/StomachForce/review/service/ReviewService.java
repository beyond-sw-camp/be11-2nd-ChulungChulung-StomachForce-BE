package com.beyond.StomachForce.review.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.review.dtos.ReviewCreateReq;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.entity.Rating;
import com.beyond.StomachForce.review.entity.Review;
import com.beyond.StomachForce.review.entity.ReviewPhoto;
import com.beyond.StomachForce.review.repository.ReviewPhotoRepository;
import com.beyond.StomachForce.review.repository.ReviewRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final UserRepository userRepository;


    public ReviewService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, ReviewPhotoRepository reviewPhotoRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.reviewPhotoRepository = reviewPhotoRepository;
        this.userRepository = userRepository;
    }

    public Long createReview(Long id, ReviewCreateReq reviewCreateReq) {
        // user 검증
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()-> new IllegalArgumentException("없는 사용자"));

        // 레스토렁 정보 확인
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("없는 레스토랑"));

        if(reviewCreateReq.getContents().length()<10){
            throw new IllegalArgumentException("리뷰 성의 없이 쓰지 마세요.");
        }
        if (reviewCreateReq.getRating() < 1 || reviewCreateReq.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
        }

        Review review = reviewRepository.save(
                Review.builder()
                        .customer(user)
                        .restaurant(restaurant)
                        .rating(Rating.fromValue(reviewCreateReq.getRating()))
                        .contents(reviewCreateReq.getContents())
                        .build()
        );

        if(reviewCreateReq.getReviewImage() != null){
            List<ReviewPhoto> reviewPhotos = reviewCreateReq.getReviewImage().stream().map(image ->{
                ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                        .reviewImagePath(saveImage(image))
                        .review(review)
                        .build();
                return reviewPhotoRepository.save(reviewPhoto);
            }).collect(Collectors.toList());
            review.getReviewPhotos().addAll(reviewPhotos);
        }
        return review.getId();
    }

    // 리뷰 이미지 저장
    public String saveImage(MultipartFile image){
        return "image_path/" + image.getOriginalFilename();
    }

    // 리스트 뽑을 메서드
    public List<ReviewListRes> findReviewsByRestaurant(Long restaurantId) {
        User user = SecurityContextHolder.getContext().getAuthentication().getName().toString();
        List<Review> reviewList = reviewRepository.findByRestaurantId(restaurantId);
        return reviewRepository.findAll().stream().map(rl->rl.fromEntity(reviewPhotoRepository.findAll().stream().map(rp->rp.fromEntity())))
    }

}
