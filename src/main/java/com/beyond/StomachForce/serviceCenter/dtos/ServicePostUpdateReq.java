package com.beyond.StomachForce.serviceCenter.dtos;

import com.beyond.StomachForce.serviceCenter.domain.select.Category;
import com.beyond.StomachForce.serviceCenter.domain.select.Visibility;
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
public class ServicePostUpdateReq {
    private Category category;
    private String title;
    private String contents;
    private Visibility visibility;
    private List<MultipartFile> photos;
}
