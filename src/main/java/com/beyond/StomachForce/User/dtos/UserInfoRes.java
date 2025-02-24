package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.Gender;
import com.beyond.StomachForce.User.domain.Enum.UserStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInfoRes {
    private Long userId;
    private String identify;
    private String userName;
    private String userNickName;
    private String userEmail;
    private String userPhoneNumber;
    private String profilePhoto;
    private String role; //홍성혁 추가
    private Gender gender;
    private UserStatus userStatus;
}
