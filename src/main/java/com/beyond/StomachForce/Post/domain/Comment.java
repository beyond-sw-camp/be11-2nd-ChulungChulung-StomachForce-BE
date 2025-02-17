package com.beyond.StomachForce.Post.domain;


import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@ToString
@Builder
public class Comment extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;
    private Long userId;
}
