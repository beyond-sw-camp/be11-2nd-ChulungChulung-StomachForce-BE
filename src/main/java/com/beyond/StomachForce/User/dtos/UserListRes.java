package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.Influencer;
import com.beyond.StomachForce.User.domain.Enum.Role;
import com.beyond.StomachForce.User.domain.Enum.UserStatus;
import com.beyond.StomachForce.User.domain.Enum.VipGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserListRes {
    private Long userId;
    private String profilePhoto;
    private String identify;
    private String email;
    private String phoneNumber;
    private VipGrade vipGrade;
    private Influencer influencer;
    private UserStatus userStatus;
    private String nickName;
    private String role;
}
