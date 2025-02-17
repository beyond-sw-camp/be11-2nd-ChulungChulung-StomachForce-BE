package com.beyond.StomachForce.serviceCenter.dtos;

import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ServiceAnswerResDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String contents;

    public ServiceAnswerResDto(ServiceAnswer answer) {
        this.id = answer.getId();
        this.postId = answer.getServicePost().getId();
        this.userId = answer.getUser().getId();
        this.contents = answer.getContents();
    }
}
