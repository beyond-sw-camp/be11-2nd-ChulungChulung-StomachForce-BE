package com.beyond.StomachForce.review.controller;

import com.beyond.StomachForce.review.dtos.ReviewCreateReq;
import com.beyond.StomachForce.review.dtos.ReviewListRes;
import com.beyond.StomachForce.review.dtos.ReviewSaveReq;
import com.beyond.StomachForce.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateReq req) {
        Long reviewId = reviewService.createReview(req);
        return new ResponseEntity<>(reviewId, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> ReviewList() {
        List<ReviewListRes> reviewListRes = reviewService.findAll();
        return new ResponseEntity<>(reviewListRes, HttpStatus.OK);
    }

}
