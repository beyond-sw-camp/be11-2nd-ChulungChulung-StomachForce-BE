package com.beyond.StomachForce.youngjae.review.dtos;

import com.beyond.StomachForce.youngjae.restaurant.entity.Restaurant;
import com.beyond.StomachForce.youngjae.review.entity.Review;
import com.beyond.StomachForce.youngjae.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewSaveReq {

    private Long RestaurantId;
//    private Long UserId;
    private String contents;
    private MultipartFile reviewImage;


}
