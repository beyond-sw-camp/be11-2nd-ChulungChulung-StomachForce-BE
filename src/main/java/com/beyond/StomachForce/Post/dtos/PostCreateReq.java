package com.beyond.StomachForce.Post.dtos;

import com.beyond.StomachForce.Post.domain.Enum.PostStatus;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.Post.domain.Post;
import jakarta.validation.constraints.NotEmpty;
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
public class PostCreateReq {
    @NotEmpty
    private String contents;
    @Builder.Default
    private PostStatus postStatus = PostStatus.Y;
    @Builder.Default
    private Long likes = 0L;
    private List<String> tags;
    private List<MultipartFile> postPhotos;

    public Post toEntity(User user){
        return Post.builder().user(user).contents(this.contents).postStatus(this.postStatus).tags(this.tags).build();
    }
}
