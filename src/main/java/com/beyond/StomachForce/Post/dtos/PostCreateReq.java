package com.beyond.StomachForce.Post.dtos;

import com.beyond.StomachForce.Post.domain.Enum.PostStatus;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.Post.domain.Post;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCreateReq {
    private Long userId;
    @NotEmpty
    private String contents;
    @Builder.Default
    private PostStatus postStatus = PostStatus.Y;
    public Post toEntity(User user){
        return Post.builder().user(user).contents(this.contents).postStatus(this.postStatus).build();
    }
}
