package com.beyond.StomachForce.announcement.dtos;

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
public class AnnouncementCreateReq {
    private String title;
    private String contents;
    private List<MultipartFile> imagePaths;
    private String endDate;
    private String type;
    public void printDebug() {
        System.out.println("📌 DTO 받은 데이터: ");
        System.out.println("Title: " + title);
        System.out.println("Contents: " + contents);
        System.out.println("endDate: " + endDate);
    }
}
