package com.beyond.StomachForce.Post.dtos;

import com.beyond.StomachForce.Post.domain.Post;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentListRes {
    private Long id;
    private String contents;
    private String userNickname;
    private String userProfile;
    private LocalDateTime updatedTime;
}
