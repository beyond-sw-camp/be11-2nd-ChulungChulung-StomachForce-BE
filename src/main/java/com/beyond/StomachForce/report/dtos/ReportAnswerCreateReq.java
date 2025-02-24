package com.beyond.StomachForce.report.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportAnswerCreateReq {
    private Long reportId;
    private String contents;
}
