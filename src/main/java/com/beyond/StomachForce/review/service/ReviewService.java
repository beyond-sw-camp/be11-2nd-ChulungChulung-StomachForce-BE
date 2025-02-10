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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final UserRepository userRepository;

    //사진의존성
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public ReviewService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, ReviewPhotoRepository reviewPhotoRepository, UserRepository userRepository, S3Client s3Client) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.reviewPhotoRepository = reviewPhotoRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
    }

    //리뷰생성
    public Long createReview(Long id, ReviewCreateReq reviewCreateReq) {
        try {
            // user 검증
            String identify = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByIdentify(identify).orElseThrow(() -> new IllegalArgumentException("없는 사용자"));

            // 레스토렁 정보 확인
            Restaurant restaurant = restaurantRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("없는 레스토랑"));

            if (reviewCreateReq.getContents().length() < 10) {
                throw new IllegalArgumentException("리뷰 성의 없이 쓰지 마세요.");
            }
            if (reviewCreateReq.getRating() < 1 || reviewCreateReq.getRating() > 5) {
                throw new IllegalArgumentException("별점은 1~5점 사이여야 합니다.");
            }
            Review review = reviewCreateReq.toEntity(user, restaurant);
            reviewRepository.save(review);
            if (reviewCreateReq.getReviewImage() != null && !reviewCreateReq.getReviewImage().isEmpty()) {
                List<ReviewPhoto> reviewPhotos = new ArrayList<>();
                for (MultipartFile file : reviewCreateReq.getReviewImage()) {
                    byte[] bytes = file.getBytes();
                    String fileName = restaurant.getName() + "_" + file.getOriginalFilename();

                    // 데스크탑 폴더에 저장
                    Path path = Paths.get("C:/Users/Playdata/Desktop/testFolder", fileName);
                    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                    // s3 저장을 위한 request 객체 생성
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .build();
                    // s3 업로드(경로를 file로 변환함)
                    s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
                    // s3 업로드된 url갖고옴
                    String s3Url =s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();

                    //리뷰사진 엔티티 생성 및 저장
                    ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                            .reviewImagePath(s3Url)
                            .review(review)
                            .build();

                    reviewPhotos.add(reviewPhotoRepository.save(reviewPhoto));
                }

                review.getReviewPhotos().addAll(reviewPhotos);
            }

            return review.getId();
        }catch (IOException e){
            throw new RuntimeException("이미지 저장 싶래",e);
        }


    }

    // 리뷰 이미지 저장
    public String saveImage(MultipartFile image){
        return "image_path/" + image.getOriginalFilename();
    }

    // 리스트 뽑을 메서드
    public List<ReviewListRes> findReviewsByRestaurant(Long restaurantId) {
        List<Review> reviewList = reviewRepository.findByRestaurantId(restaurantId);
        return reviewList.stream().map(review ->
                ReviewListRes.builder()
                        .id(review.getId())
                        .contents(review.getContents())
                        .memberEmail(review.getCustomer().getEmail())
                        .createdTime(review.getCreatedTime())
                        .updatedTime(review.getUpdatedTime())
                        .build()
        ).collect(Collectors.toList());
    }

}
