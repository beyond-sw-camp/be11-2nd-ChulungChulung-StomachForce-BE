package com.beyond.StomachForce.review.controller;

import com.beyond.StomachForce.review.service.ReviewService;
import org.springframework.web.bind.annotation.*;

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
