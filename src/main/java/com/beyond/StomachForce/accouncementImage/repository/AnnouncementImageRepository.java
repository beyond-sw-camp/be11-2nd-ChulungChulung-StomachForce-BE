package com.beyond.StomachForce.accouncementImage.repository;

import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.announcement.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementImageRepository extends JpaRepository<AnnouncementImage, Long> {
    void deleteAllByAnnouncement(Announcement announcement);
}
