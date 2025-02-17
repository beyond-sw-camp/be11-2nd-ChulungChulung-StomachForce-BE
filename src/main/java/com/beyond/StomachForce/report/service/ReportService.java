package com.beyond.StomachForce.report.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.report.domain.Report;
import com.beyond.StomachForce.report.dtos.AdminCommentUpdateReq;
import com.beyond.StomachForce.report.dtos.ReportCreateReq;
import com.beyond.StomachForce.report.dtos.ReportResDto;
import com.beyond.StomachForce.report.dtos.ReportUpdateReq;
import com.beyond.StomachForce.report.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;


    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public ReportResDto createReport(ReportCreateReq req) {
        User reporter = userRepository.findById(req.getReporterId())
                .orElseThrow(() -> new EntityNotFoundException("신고한 사용자가 존재하지 않습니다."));
        User reported = userRepository.findById(req.getReportedId())
                .orElseThrow(() -> new EntityNotFoundException("신고된 사용자가 존재하지 않습니다."));

        Report report = Report.builder()
                .reporter(reporter)
                .reported(reported)
                .reportClass(req.getReportClass())
                .contents(req.getContents())
                .photo(req.getPhoto())
                .adminComment(null) // 초기에는 관리자 코멘트 없음
                .build();

        reportRepository.save(report);
        return new ReportResDto(report);
    }

    public ReportResDto updateReport(Long reportId, ReportUpdateReq req) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));

        if (req.getReportClass() != null) report.setReportClass(req.getReportClass());
        if (req.getContents() != null) report.setContents(req.getContents());
        if (req.getPhoto() != null) report.setPhoto(req.getPhoto());

        return new ReportResDto(report);
    }

    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));
        reportRepository.delete(report);
    }

    public ReportResDto updateAdminComment(Long reportId, AdminCommentUpdateReq req) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));

        report.setAdminComment(req.getAdminComment());
        return new ReportResDto(report);
    }
}
