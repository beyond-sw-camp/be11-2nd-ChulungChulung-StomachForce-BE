package com.beyond.StomachForce.report.controller;

import com.beyond.StomachForce.report.dtos.*;
import com.beyond.StomachForce.report.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportResDto> createPost(
            @ModelAttribute ReportCreateReq req,
            Authentication authentication // 현재 로그인된 사용자 정보 가져오기
    ) {
        String userIdentify = authentication.getName(); // 예: 이메일 또는 고유 식별자
        return ResponseEntity.ok(reportService.createReport(req, userIdentify));
    }


    @PatchMapping(value = "/update/{reportId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportResDto> updatePost(
            @PathVariable Long reportId,
            @ModelAttribute ReportUpdateReq req
    ) {
        return ResponseEntity.ok(reportService.updateReport(reportId, req));
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<String> deletePost(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResDto> getPostById(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReportResDto>> getAllPosts() {
        return ResponseEntity.ok(reportService.getAllPosts());
    }

    //answer

    @GetMapping("/answer/{reportId}")
    public ResponseEntity<ReportAnswerResDto> getAnswerByPostId(@PathVariable Long reportId) {
        ReportAnswerResDto answer = reportService.getAnswerByReportId(reportId);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/answer/create")
    public ResponseEntity<ReportAnswerResDto> createAnswer(@RequestBody ReportAnswerCreateReq req) {
        return ResponseEntity.ok(reportService.createAnswer(req));
    }

    @PatchMapping("/answer/update/{answerId}")
    public ResponseEntity<ReportAnswerResDto> updateAnswer(@PathVariable Long answerId, @RequestBody ReportAnswerUpdateReq req) {
        return ResponseEntity.ok(reportService.updateAnswer(answerId, req));
    }

    @DeleteMapping("/answer/delete/{answerId}")
    public ResponseEntity<String> deleteAnswer(@PathVariable Long answerId) {
        reportService.deleteAnswer(answerId);
        return ResponseEntity.ok("답변이 삭제되었습니다.");
    }
}
