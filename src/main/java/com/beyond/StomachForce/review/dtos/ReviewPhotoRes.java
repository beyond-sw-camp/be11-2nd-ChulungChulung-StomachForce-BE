package com.beyond.StomachForce.review.dtos;

import com.beyond.StomachForce.User.domain.User;
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
    private User user;

}
