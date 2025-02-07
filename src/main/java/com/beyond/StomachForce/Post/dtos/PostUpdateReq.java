package com.beyond.StomachForce.Post.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostUpdateReq {
    private Long id;
    @NotEmpty
    private String contents;
}
