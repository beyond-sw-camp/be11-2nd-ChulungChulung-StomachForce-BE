package com.beyond.StomachForce.announcement.dtos;


import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.announcement.domain.AnnounceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AnnouncementUpdateReq {
    private String title;
    private String contents;
    private AnnounceStatus status;
    private List<MultipartFile> images;
}
