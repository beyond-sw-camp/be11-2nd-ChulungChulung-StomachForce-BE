package com.beyond.StomachForce.announcement.repository;

import com.beyond.StomachForce.announcement.domain.AnnounceStatus;
import com.beyond.StomachForce.announcement.domain.Announcement;
import com.beyond.StomachForce.announcement.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTypeAndStatus(Type type, AnnounceStatus status);

}
