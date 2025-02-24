package com.beyond.StomachForce.report.dtos;

import com.beyond.StomachForce.report.domain.select.ReportClass;
import com.beyond.StomachForce.serviceCenter.domain.select.Category;
import com.beyond.StomachForce.serviceCenter.domain.select.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportCreateReq {
    private Long reporterId;
    private Long reportedId;
    private ReportClass reportClass;
    private String contents;
    private List<MultipartFile> photos;
}
