package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor //기본생성자
@AllArgsConstructor //모든 매개변수있는 생성자
@Data
@Builder
public class UserUpdateReq {
    @NotEmpty
    private String identify;
    @NotEmpty
    @Size(min=8)
    private String password;
    @NotEmpty
    private String name;
    @NotEmpty
    private String nickName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String phoneNumber;
    @Builder.Default
    private Gender gender = Gender.FEMALE;
    private String profilePhoto;
}
