package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.VipGrade;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VipBenefitRegistDto {
    private VipGrade vipGrade;
    private String title;
    private String contents;
    private MultipartFile benefitPhoto;
}
