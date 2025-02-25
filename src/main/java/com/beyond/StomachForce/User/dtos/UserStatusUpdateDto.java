package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.Influencer;
import com.beyond.StomachForce.User.domain.Enum.UserStatus;
import com.beyond.StomachForce.User.domain.Enum.VipGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserStatusUpdateDto {
    private VipGrade vipGrade;
    private Influencer influencer;
    private UserStatus userStatus;
}
