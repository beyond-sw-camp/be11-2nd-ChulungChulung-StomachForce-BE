package com.beyond.StomachForce.youngjae.review.controller;

import com.beyond.StomachForce.youngjae.review.dtos.ReviewListRes;
import com.beyond.StomachForce.youngjae.review.dtos.ReviewSaveReq;
import com.beyond.StomachForce.youngjae.review.entity.Review;
import com.beyond.StomachForce.youngjae.review.service.ReviewService;
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

    //로그인 만드는 동안 잠깐 주석합시다요
//    @PostMapping("/create")
//    public ResponseEntity<?> PostCreateReview(@Valid @RequestBody ReviewSaveReq reviewSaveReq) {
//        Long reviewId = reviewService.save(reviewSaveReq);
//        return new ResponseEntity<>(reviewId,HttpStatus.CREATED);
//    }
//
//    @GetMapping("/list")
//    public ResponseEntity<?> ReviewList() {
//        List<ReviewListRes> reviewListRes = reviewService.findAll();
//        return new ResponseEntity<>(reviewListRes, HttpStatus.OK);
//    }

}
