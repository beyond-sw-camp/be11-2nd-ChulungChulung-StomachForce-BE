package com.beyond.StomachForce.report.dtos;

import com.beyond.StomachForce.report.domain.Report;
import com.beyond.StomachForce.report.domain.select.ReportClass;
import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> photos;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public ReportResDto(Report report) {
        this.id = report.getId();
        this.reporterId = report.getReporter().getId();
        this.reportedId = report.getReported();
        this.reportClass = report.getReportClass().name();
        this.contents = report.getContents();
        this.photos = report.getReportPhotos().stream()
                .map(photo -> photo.getPhoto())
                .collect(Collectors.toList());
        this.createdTime = report.getCreatedTime();
        this.updatedTime = report.getUpdatedTime();
    }
}
