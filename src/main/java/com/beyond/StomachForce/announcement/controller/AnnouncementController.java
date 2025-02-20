package com.beyond.StomachForce.announcement.controller;

import com.beyond.StomachForce.announcement.domain.Announcement;
import com.beyond.StomachForce.announcement.dtos.AnnouncementCreateReq;
import com.beyond.StomachForce.announcement.service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }
    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(AnnouncementCreateReq dto) throws IOException {
        Announcement announcement = announcementService.createAnnouncement(dto);
        return ResponseEntity.ok(announcement);
    }
}
