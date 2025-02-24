package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.dtos.MyPostDto;
import com.beyond.StomachForce.User.domain.Enum.Influencer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MypageRes {
    private String nickName;
    private String email;
    private Influencer influencer;
    private List<String> postPhotos;
    private List<MyPostDto> postIds;
    private Integer totalPost;

}
