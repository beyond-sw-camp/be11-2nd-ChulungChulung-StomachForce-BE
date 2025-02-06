package com.beyond.StomachForce.review.dtos;

import jakarta.persistence.GeneratedValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewPhotoRes {

    private String photoUrl;
//    private UserId uesr;

}
