package com.beyond.StomachForce.serviceCenter.dtos;

import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ServiceAnswerResDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String contents;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public ServiceAnswerResDto(ServiceAnswer answer) {
        this.id = answer.getId();
        this.postId = answer.getServicePost().getId();
        this.contents = answer.getContents();
        this.createdTime = answer.getCreatedTime();
        this.updatedTime = answer.getUpdatedTime();
    }
}
