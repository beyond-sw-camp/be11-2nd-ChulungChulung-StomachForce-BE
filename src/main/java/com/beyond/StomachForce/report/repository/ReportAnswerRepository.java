package com.beyond.StomachForce.report.repository;

import com.beyond.StomachForce.report.domain.ReportAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportAnswerRepository extends JpaRepository<ReportAnswer, Long> {
    Optional<ReportAnswer> findByReportId(Long reportId);
}
