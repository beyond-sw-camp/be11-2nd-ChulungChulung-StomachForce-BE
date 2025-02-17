package com.beyond.StomachForce.announcement.repository;

import com.beyond.StomachForce.announcement.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

}
