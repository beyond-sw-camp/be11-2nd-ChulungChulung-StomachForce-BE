package com.beyond.StomachForce.User.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.User.domain.Enum.*;
import com.beyond.StomachForce.User.dtos.*;
import com.beyond.StomachForce.report.domain.Report;
import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import com.beyond.StomachForce.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
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
//    @Column(nullable = false)
    private Long mileageBalance;
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
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservationList;//홍성혁 추가 - user의 예약내역확인.
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAddress> userAddresses = new ArrayList<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Builder.Default
    private List<ServicePost> servicePosts = new ArrayList<>();
    @OneToMany(mappedBy = "reporter",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Report> reportsMade = new ArrayList<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Follower> followers = new ArrayList<>();
    @OneToMany(mappedBy = "followerUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Follower> following = new ArrayList<>();



    public void updateUser(UserUpdateReq userUpdateReq, String s3Url){
        this.name = userUpdateReq.getName();
        this.nickName = userUpdateReq.getNickName();
        this.email = userUpdateReq.getEmail();
        this.phoneNumber = userUpdateReq.getPhoneNumber();
        this.gender = userUpdateReq.getGender();
        this.profilePhoto = s3Url;
    }

    public void userStop(){
        this.userStatus = UserStatus.S;
    }

    public void mileageUpdate(Long mileageBalance){
        this.mileageBalance = mileageBalance;
    }

    public void followerAdd(Follower follower){
        this.followers.add(follower);
    }
    public void followingAdd(Follower follower) {
        this.following.add(follower);
    }


    public List<FollowerListRes> followerList(){
        List<FollowerListRes> follwerList = new ArrayList<>();
        for(Follower f: followers){
            FollowerListRes followerListRes = FollowerListRes.builder()
                    .userId(f.getFollowerUser().getId())
                    .userName(f.getFollowerUser().getName())
                    .userProfile(f.getFollowerUser().getProfilePhoto())
                    .build();
            follwerList.add(followerListRes);
        }
        return follwerList;
    }

    public List<FollowingListRes> followingList(){
        List<FollowingListRes> follwingList = new ArrayList<>();
        for(Follower f:following){
            FollowingListRes followingListRes = FollowingListRes.builder()
                    .userId(f.getUser().getId())
                    .userName(f.getUser().getName())
                    .userProfile(f.getUser().getProfilePhoto()).build();
            follwingList.add(followingListRes);
        }
        return follwingList;
    }


    public void updateImagePath(String imagePath){
        this.profilePhoto = imagePath;
    }

    public List<Post> postList(){
        List<Post> userPostList = new ArrayList<>();
        for(Post p: posts){
            Post post = Post.builder()
                    .contents(p.getContents())
                    .tags(p.getTags())
                    .postPhotos(p.getPostPhotos())
                    .build();
            userPostList.add(post);
        }
        return userPostList;
    }

    public UserInfoRes userInfoRes(){
        return UserInfoRes.builder()
                .userNickName(this.nickName)
                .userName(this.getName())
                .profilePhoto(this.getProfilePhoto())
                .userId(this.getId())
                .role(String.valueOf(this.getRole()))
                .build();
    }

}
