package com.beyond.StomachForce.review.dtos;

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

    private Long restaurantId;
    @NotBlank(message = "비울 수 없는 항목입니다.")
    private String contents;
    private int rating;
    private List<MultipartFile> reviewImage;


}
