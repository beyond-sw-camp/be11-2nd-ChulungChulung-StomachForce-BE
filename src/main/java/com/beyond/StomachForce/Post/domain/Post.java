package com.beyond.StomachForce.Post.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Post.domain.Enum.PostStatus;
import com.beyond.StomachForce.Post.dtos.CommentCreateDto;
import com.beyond.StomachForce.Post.dtos.PostDetailRes;
import com.beyond.StomachForce.Post.dtos.PostUpdateReq;
import com.beyond.StomachForce.User.domain.Follower;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.UserAddress;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Post extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String contents;
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Builder.Default
    private Long likes = 0L;
//    @Builder.Default
//    private List<Long>likedUser = new ArrayList<>();
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private List<String> postPhotos = new ArrayList<>();
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void updatePost(PostUpdateReq postUpdateReq){
        this.contents = postUpdateReq.getContents();
    }

    public void deletePost(){
        this.postStatus = PostStatus.N;
    }

    public void updateLike(Long likes){
        this.likes = likes;
    }

    public void updatePostImagePath(String postPhotos){
//        this.postPhotos.add(postPhotos);
        this.postPhotos.add(postPhotos);
    }

    public PostDetailRes postDetails(Long likes, LocalDateTime createdTime){
        PostDetailRes postDetailRes = PostDetailRes.builder().
                contents(this.contents).
                likes(likes).
                postPhotos(this.postPhotos).
                tags(this.tags).
                createdTime(createdTime).
                build();
        return postDetailRes;
    }
}
