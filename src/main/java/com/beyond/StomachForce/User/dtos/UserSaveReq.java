package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.*;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.UserAddress;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserSaveReq {
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
    private String birth;
    @Builder.Default
    private Gender gender = Gender.FEMALE;
    @Builder.Default
    private Long mileageBalance = 0L;
    @Builder.Default
    private Influencer influencer = Influencer.N;
    @Builder.Default
    private UserStatus userStatus = UserStatus.Y;
    @Builder.Default
    private VipGrade vipGrade = VipGrade.D;
    @Builder.Default
    private Role role = Role.USER;
    private UserAddress userAddress;

    public User toEntity(String encodedPassword) {
        return User.builder().name(this.name).nickName(this.nickName).identify(this.identify).password(encodedPassword).
                phoneNumber(this.phoneNumber).birth(this.birth).gender(this.gender).email(this.email).
                mileageBalance(this.mileageBalance).influencer(this.influencer).userStatus(this.userStatus).
                vipGrade(this.vipGrade).role(this.role).build();
    }
}
