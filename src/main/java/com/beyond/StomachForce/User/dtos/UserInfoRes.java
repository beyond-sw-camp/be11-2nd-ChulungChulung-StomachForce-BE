package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.Gender;
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
    private Gender gender;
}
