package com.beyond.StomachForce.review.controller;

import com.beyond.StomachForce.review.dtos.ReviewCreateReq;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review/{restaurantId}/create")
    public ResponseEntity<?> createReview(@PathVariable Long restaurantId, @Valid @RequestBody ReviewCreateReq req) {
        Long reviewId = reviewService.createReview(restaurantId, req);
        return new ResponseEntity<>(reviewId, HttpStatus.CREATED);
    }

    @GetMapping("/review/{restaurantId}/list")
    public ResponseEntity<?> ReviewList(@PathVariable Long restaurantId) {
        List<ReviewListRes> reviewListRes = reviewService.findReviewsByRestaurant(restaurantId);
        return new ResponseEntity<>(reviewListRes, HttpStatus.OK);
    }

}
