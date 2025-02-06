package com.beyond.StomachForce.User.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.User.domain.Enum.*;
import com.beyond.StomachForce.User.dtos.UserUpdateReq;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@ToString
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,length =20,nullable = false)
    private String identify;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nickName;
    private String email;
    @Column(nullable = false)
    private String phoneNumber;
//    @Column(nullable = false)
    private String birth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String profilePhoto;
    @Column(nullable = false)
    private String mileageBalance;
    @Enumerated(EnumType.STRING)
    private VipGrade vipGrade;
    @Enumerated(EnumType.STRING)
    private Influencer influencer;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    public void updateUser(UserUpdateReq userUpdateReq){
        this.identify = userUpdateReq.getIdentify();
        this.password = userUpdateReq.getPassword();
        this.name = userUpdateReq.getName();
        this.nickName = userUpdateReq.getNickName();
        this.email = userUpdateReq.getEmail();
        this.phoneNumber = userUpdateReq.getPhoneNumber();
        this.gender = userUpdateReq.getGender();
        this.profilePhoto = userUpdateReq.getProfilePhoto();
    }

    public void userStop(){
        this.userStatus = UserStatus.S;
    }
}
