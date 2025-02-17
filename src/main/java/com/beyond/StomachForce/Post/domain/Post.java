package com.beyond.StomachForce.Post.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Post.domain.Enum.PostStatus;
import com.beyond.StomachForce.Post.dtos.PostUpdateReq;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.UserAddress;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@ToString
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
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    @Builder.Default
    private List<PostPhotos> postPhotos = new ArrayList<>();

    public void updatePost(PostUpdateReq postUpdateReq){
        this.contents = postUpdateReq.getContents();
    }

    public void deletePost(){
        this.postStatus = PostStatus.N;
    }

    public void updateLike(Long likes){
        this.likes = likes;
    }

    public void updatePostImagePath(PostPhotos postPhotos){
        this.postPhotos.add(postPhotos);
    }
}
