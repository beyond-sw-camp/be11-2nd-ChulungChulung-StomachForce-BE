package com.beyond.StomachForce.report.repository;

import com.beyond.StomachForce.report.domain.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto,Long> {
}
