package com.beyond.StomachForce.review.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.review.dtos.ReviewCreateReq;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.dtos.ReviewUpdateReq;
import com.beyond.StomachForce.review.entity.Rating;
import com.beyond.StomachForce.review.entity.Review;
import com.beyond.StomachForce.review.entity.ReviewPhoto;
import com.beyond.StomachForce.review.repository.ReviewRepository;
import com.beyond.StomachForce.review.repository.ReviewPhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client;
    private final String bucket = "your-s3-bucket-name";

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewPhotoRepository reviewPhotoRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            S3Client s3Client) {
        this.reviewRepository = reviewRepository;
        this.reviewPhotoRepository = reviewPhotoRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
    }
    // SecurityContextHolder 안쓰는게 뭐 모킹이 쉬워지고 테스트하기 쉽고 책임분리가 잘되서 Authentication 쓰길래 썻는데 우선 제개
    // 이거 코드 리뷰좀 해야할 것 같습니다.
    public void reviewCreate(Long restaurantId, ReviewCreateReq req) {
        String userIdentify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .rating(Rating.fromValue(req.getRating()))
                .contents(req.getContents())
                .build();

        reviewRepository.save(review);

        if (req.getReviewImage() != null) {
            saveReviewPhotos(review, req.getReviewImage());
        }
    }

    public List<ReviewListRes> reviewList(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(Review::toListDto) // 엔티티 내부에서 변환 처리
                .collect(Collectors.toList());
    }

    public void updateReview(Long restaurantId, Long reviewId, ReviewUpdateReq req) {
        String userIdentify = SecurityContextHolder.getContext().getAuthentication().getName();
        Review review = reviewRepository.findByIdAndRestaurantId(reviewId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getUser().getIdentify().equals(userIdentify)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        review.updateReview(req.getContents(), req.getRating()); // 기존 코드 유지

        if (req.getReviewPhotos() != null) {
            saveReviewPhotos(review, req.getReviewPhotos());
        }

        if (req.getReviewPhotoRemove() != null) {
            deleteReviewPhotos(req.getReviewPhotoRemove());
        }
    }

    public void deleteReview(Long restaurantId, Long reviewId, Authentication authentication) {
        String userIdentify = authentication.getName();
        Review review = reviewRepository.findByIdAndRestaurantId(reviewId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getUser().getIdentify().equals(userIdentify)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        reviewRepository.delete(review);
    }

    private void saveReviewPhotos(Review review, List<MultipartFile> reviewImages) {
        List<ReviewPhoto> reviewPhotos = new ArrayList<>();

        for (MultipartFile image : reviewImages) {
            try {
                String fileName = review.getId() + "_" + image.getOriginalFilename();
                Path path = Paths.get("C:/Users/Playdata/Desktop/testFolder", fileName);
                Files.write(path, image.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));

                String s3Url = s3Client.utilities().getUrl(r -> r.bucket(bucket).key(fileName)).toExternalForm();

                ReviewPhoto reviewPhoto = new ReviewPhoto(review, s3Url);
                reviewPhotos.add(reviewPhoto);
            } catch (IOException e) {
                throw new RuntimeException("Image upload failed");
            }
        }

        reviewPhotoRepository.saveAll(reviewPhotos);
    }

    private void deleteReviewPhotos(List<String> photoUrlsToRemove) {
        List<ReviewPhoto> reviewPhotos = reviewPhotoRepository.findByReviewImagePathIn(photoUrlsToRemove);
        reviewPhotoRepository.deleteAll(reviewPhotos);
    }
}