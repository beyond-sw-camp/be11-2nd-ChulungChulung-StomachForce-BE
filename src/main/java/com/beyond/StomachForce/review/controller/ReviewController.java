package com.beyond.StomachForce.review.controller;

import com.beyond.StomachForce.review.dtos.ReviewCreateReq;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.dtos.ReviewUpdateReq;
import com.beyond.StomachForce.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant/{restaurantId}/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReview(
            @PathVariable Long restaurantId,
            @Valid ReviewCreateReq req) {

        reviewService.reviewCreate(restaurantId, req);
        return new ResponseEntity<>("Review created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReviewListRes>> getReviews(@PathVariable Long restaurantId) {
        List<ReviewListRes> reviews = reviewService.reviewList(restaurantId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PatchMapping("/{reviewId}/update")
    public ResponseEntity<?> updateReview(
            @PathVariable Long restaurantId,
            @PathVariable Long reviewId,
            @Valid ReviewUpdateReq req,
            Authentication authentication) {

        reviewService.updateReview(restaurantId, reviewId, req);
        return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}/delete")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long restaurantId,
            @PathVariable Long reviewId,
            Authentication authentication) {

        reviewService.deleteReview(restaurantId, reviewId, authentication);
        return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
    }
}