package com.beyond.StomachForce.announcement.dtos;

import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AnnouncementDetailRes {
    private String title;
    private LocalDate creaetedDate;
    private String announcementType;
    private String contents;
    private List<AnnouncementImage> images;
}
