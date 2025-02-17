package com.beyond.StomachForce.report.dtos;

import com.beyond.StomachForce.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportResDto {
    private Long id;
    private Long reporterId;
    private Long reportedId;
    private String reportClass;
    private String contents;
    private String photo;
    private String adminComment;

    public ReportResDto(Report report) {
        this.id = report.getId();
        this.reporterId = report.getReporter().getId();
        this.reportedId = report.getReported().getId();
        this.reportClass = report.getReportClass().name();
        this.contents = report.getContents();
        this.photo = report.getPhoto();
        this.adminComment = report.getAdminComment();
    }
}
