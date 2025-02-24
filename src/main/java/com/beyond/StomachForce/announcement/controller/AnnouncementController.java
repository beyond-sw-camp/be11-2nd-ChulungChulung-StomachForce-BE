package com.beyond.StomachForce.announcement.controller;

import com.beyond.StomachForce.User.domain.Enum.Role;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.announcement.domain.Announcement;
import com.beyond.StomachForce.announcement.dtos.AnnouncementCreateReq;
import com.beyond.StomachForce.announcement.dtos.AnnouncementDetailRes;
import com.beyond.StomachForce.announcement.dtos.AnnouncementUpdateReq;
import com.beyond.StomachForce.announcement.dtos.EventBannerRes;
import com.beyond.StomachForce.announcement.service.AnnouncementService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final UserRepository userRepository;

    public AnnouncementController(AnnouncementService announcementService, UserRepository userRepository) {
        this.announcementService = announcementService;
        this.userRepository = userRepository;
    }
    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(AnnouncementCreateReq dto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()-> new EntityNotFoundException("user is not found"));
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        Announcement announcement = announcementService.createAnnouncement(dto);
        return ResponseEntity.ok(announcement);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateAnnouncement(AnnouncementUpdateReq dto, @PathVariable("id") Long announcementId) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()-> new EntityNotFoundException("user is not found"));
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }
        Announcement announcement = announcementService.updateAnnouncement(dto, announcementId);
        return ResponseEntity.ok(announcement);
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()-> new EntityNotFoundException("user is not found"));
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok("공지사항이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAnnouncement(){
        return ResponseEntity.ok(announcementService.getAnnouncements());
    }
    @GetMapping("/event/ongoing")
    public ResponseEntity<List<EventBannerRes>> getOngoingEvents() {
        List<EventBannerRes> events = announcementService.getOngoingEvents();
        return ResponseEntity.ok(events);
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<AnnouncementDetailRes> getAnnouncementDetail(@PathVariable Long id) {
        AnnouncementDetailRes announcementDetail = announcementService.getAnnouncementDetail(id);
        return ResponseEntity.ok(announcementDetail);
    }

}
