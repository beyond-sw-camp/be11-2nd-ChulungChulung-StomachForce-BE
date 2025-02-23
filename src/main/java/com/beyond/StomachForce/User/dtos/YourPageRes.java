package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.Post.dtos.MyPostDto;
import com.beyond.StomachForce.User.domain.Enum.Influencer;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class YourPageRes {
    private String profile;
    private Integer follwers;
    private Integer followings;
    private String nickName;
    private String email;
    private Influencer influencer;
    private List<String> postPhotos = new ArrayList<>();
    private Integer totalPost;
    private List<MyPostDto> postIds;
    private Boolean isFollowing;
}
