package com.beyond.StomachForce.announcement.dtos;

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
public class AnnouncementCreateReq {
    private String title;
    private String contents;
    private List<MultipartFile> imagePaths;
}
