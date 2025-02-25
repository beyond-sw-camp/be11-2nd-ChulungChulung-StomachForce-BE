package com.beyond.StomachForce.review.dtos;

import com.beyond.StomachForce.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewListRes {
    private Long id;
    private String contents;
    private String RestaurantName;
    private String userIdentify;
    private Integer rating;
    private List<String> reviewPhotos;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
