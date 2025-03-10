package com.beyond.StomachForce.review.dtos;

import com.beyond.StomachForce.review.entity.Rating;
import com.beyond.StomachForce.review.entity.Review;
import com.beyond.StomachForce.review.entity.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewUpdateReq {
    private String contents;                  // 리뷰내용
    private int rating;                   // 별점
    private List<MultipartFile> reviewPhotos; // 새로 추가할 사진
    private List<String> reviewPhotoRemove;  // 삭제할 사진 url 리스트 추가

    public Rating getRatingEnum() {
        return Rating.fromValue(this.rating);
    }


}
