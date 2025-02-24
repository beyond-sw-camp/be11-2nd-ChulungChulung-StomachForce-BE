package com.beyond.StomachForce.report.dtos;

import com.beyond.StomachForce.report.domain.ReportAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportAnswerResDto {
    private Long id;
    private Long postId;
    private String contents;

    public ReportAnswerResDto(ReportAnswer answer) {
        this.id = answer.getId();
        this.postId = answer.getReport().getId();
        this.contents = answer.getContents();
    }
}
