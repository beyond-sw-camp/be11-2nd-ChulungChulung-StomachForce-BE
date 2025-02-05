package com.beyond.StomachForce.youngjae.review.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewUpdateReq {
    private String contents;
    private MultipartFile reviewImage;
}
