package com.beyond.StomachForce.review.dtos;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.review.entity.Rating;
import com.beyond.StomachForce.review.entity.Review;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewCreateReq {

    @NotBlank(message = "비울 수 없는 항목입니다.")
    private String contents;
    private Integer rating;
    private List<MultipartFile> reviewImage;

    public Review toEntity(Restaurant restaurant) {
        User user = User.builder()
                .build();
        return Review.builder()
                .user(user)
                .restaurant(restaurant)
                .rating(Rating.fromValue(this.rating))
                .contents(this.contents)
                .build();
    }
}
