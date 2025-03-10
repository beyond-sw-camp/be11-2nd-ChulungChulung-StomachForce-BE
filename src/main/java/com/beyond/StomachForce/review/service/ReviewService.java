package com.beyond.StomachForce.review.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.reservation.domain.Reservation;
import com.beyond.StomachForce.reservation.repository.ReservationRepository;
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
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket ;

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewPhotoRepository reviewPhotoRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository, ReservationRepository reservationRepository,
            S3Client s3Client) {
        this.reviewRepository = reviewRepository;
        this.reviewPhotoRepository = reviewPhotoRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
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

        // ✅ 해당 유저가 이 레스토랑을 예약한 적이 있는지 확인
        List<Reservation> reservations = reservationRepository.findCompletedReservations(user, restaurant);
        if (reservations.isEmpty()) {
            throw new IllegalStateException("해당 레스토랑에 대한 예약 기록이 없습니다. 예약 후 리뷰를 작성하세요.");
        }

        // 예약 시간 이후인지 확인 (예약 시간이 현재보다 과거여야 함)
        boolean hasPastReservation = reservations.stream()
                .anyMatch(reservation ->
                        reservation.getReservationDate().isBefore(LocalDate.now()) || // 예약 날짜가 오늘 이전
                                (reservation.getReservationDate().isEqual(LocalDate.now()) &&
                                        reservation.getReservationTime().isBefore(LocalTime.now())) // 예약 날짜가 오늘이고, 시간이 현재보다 과거
                );

        if (!hasPastReservation) {
            throw new IllegalStateException("예약 시간이 지나야 리뷰를 작성할 수 있습니다.");
        }

        // 해당 레스토랑에 대해 이미 리뷰를 작성했는지 확인
        boolean alreadyReviewed = reviewRepository.existsByUserAndRestaurant(user, restaurant);
        if (alreadyReviewed) {
            throw new IllegalStateException("이 레스토랑에 대한 리뷰는 이미 작성되었습니다.");
        }

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
        return reviewRepository.findByRestaurantIdOrderByCreatedTimeDesc(restaurantId)
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

        review.updateReview(req.getContents(), req.getRatingEnum()); // 기존 코드 유지

        if (req.getReviewPhotos() != null && !req.getReviewPhotos().isEmpty()) {
            saveReviewPhotos(review, req.getReviewPhotos());
        }

        if (req.getReviewPhotoRemove() != null) {
            deleteReviewPhotos(reviewId, req.getReviewPhotoRemove());
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

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType(image.getContentType()) // MIME 타입 지정 (선택)
                        .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

                String s3Url = s3Client.utilities().getUrl(r -> r.bucket(bucket).key(fileName)).toExternalForm();

                ReviewPhoto reviewPhoto = new ReviewPhoto(review, s3Url);
                reviewPhotos.add(reviewPhoto);
            } catch (IOException e) {
                throw new RuntimeException("사진 저장에 실패했습니다.");
            }
        }

        reviewPhotoRepository.saveAll(reviewPhotos);
    }

    private void deleteReviewPhotos(Long reviewId ,List<String> photoUrlsToRemove) {
        List<ReviewPhoto> reviewPhotos = reviewPhotoRepository.findByReviewImagePathIn(photoUrlsToRemove);

        // 다른 User의 사진을 삭제할 수 있음. 왜냐면 이미지 구조 자체가 url로 돼있어서 임의의 사진이 삭제될 수 있음.
        List<ReviewPhoto> photosDelete = reviewPhotos.stream()
                .filter(photo -> photo.getReview().getId().equals(reviewId))
                .collect(Collectors.toList());
        reviewPhotoRepository.deleteAll(photosDelete);
    }
}