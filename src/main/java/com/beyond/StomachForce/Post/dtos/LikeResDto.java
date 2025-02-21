package com.beyond.StomachForce.Post.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LikeResDto {
    private Long postId;
    private Long likes;
    private boolean isLiked;
}
