package com.beyond.StomachForce.User.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FollowerListRes {
    private Long userId;
    private String userNickName;
    private String userProfile;
}
