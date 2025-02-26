package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MyPhotoRes {
    private Long photoId;
    private String photoUrl;
}
