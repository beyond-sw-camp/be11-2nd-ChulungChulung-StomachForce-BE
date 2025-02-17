package com.beyond.StomachForce.report.controller;

import com.beyond.StomachForce.report.dtos.AdminCommentUpdateReq;
import com.beyond.StomachForce.report.dtos.ReportCreateReq;
import com.beyond.StomachForce.report.dtos.ReportResDto;
import com.beyond.StomachForce.report.dtos.ReportUpdateReq;
import com.beyond.StomachForce.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/create")
    public ResponseEntity<ReportResDto> createReport(@RequestBody ReportCreateReq req) {
        return ResponseEntity.ok(reportService.createReport(req));
    }

    @PatchMapping("/update/{reportId}")
    public ResponseEntity<ReportResDto> updateReport(@PathVariable Long reportId, @RequestBody ReportUpdateReq req) {
        return ResponseEntity.ok(reportService.updateReport(reportId, req));
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("신고가 삭제되었습니다.");
    }

    @PatchMapping("/admin-comment/{reportId}")
    public ResponseEntity<ReportResDto> updateAdminComment(@PathVariable Long reportId, @RequestBody AdminCommentUpdateReq req) {
        return ResponseEntity.ok(reportService.updateAdminComment(reportId, req));
    }
}
