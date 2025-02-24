package com.beyond.StomachForce.menu.dto;

import com.beyond.StomachForce.menu.domain.AllergyInfo;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MenuUpdateDto {
    private String name;
    private Long price;
    private String description;
    private MultipartFile menuPhoto;
    private AllergyInfo allergyInfo;
}
