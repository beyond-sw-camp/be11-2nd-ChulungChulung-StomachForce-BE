package com.beyond.StomachForce.report.dtos;

import com.beyond.StomachForce.report.domain.select.ReportClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportUpdateReq {
    private ReportClass reportClass;
    private String contents;
    private String photo;
}
