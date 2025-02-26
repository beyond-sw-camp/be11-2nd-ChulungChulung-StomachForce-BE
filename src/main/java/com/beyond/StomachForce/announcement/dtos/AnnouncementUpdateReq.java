package com.beyond.StomachForce.announcement.dtos;


import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.announcement.domain.AnnounceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AnnouncementUpdateReq {
    private String title;
    private String contents;
    private AnnounceStatus status;
    private String endDate;
    private List<MultipartFile> images;
    private String keepExistingImages;  // "true" 또는 "false"
    private List<Long> existingImageIds;
}
