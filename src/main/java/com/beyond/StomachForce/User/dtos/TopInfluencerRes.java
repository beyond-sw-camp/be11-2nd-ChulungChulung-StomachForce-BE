package com.beyond.StomachForce.User.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopInfluencerRes {
    private Long userId;
    private String profileImage;
    private String nickname;
    private int followersCount;
}
