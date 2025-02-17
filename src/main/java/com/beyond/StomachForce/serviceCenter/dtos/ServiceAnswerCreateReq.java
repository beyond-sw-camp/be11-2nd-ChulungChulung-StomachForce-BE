package com.beyond.StomachForce.serviceCenter.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ServiceAnswerCreateReq {
    private Long userId;
    private Long postId;
    private String contents;
}
