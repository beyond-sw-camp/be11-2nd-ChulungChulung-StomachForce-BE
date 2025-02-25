package com.beyond.StomachForce.Post.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostDetailRes {
    private Long postId;
    private String contents;
    private Long likes;
    private List<String> tags;
    private List<String> postPhotos;
    private List<Long> likedUser;
    private String userNickName;
    private String userProfile;
    private LocalDateTime createdTime;
}
