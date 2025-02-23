package com.beyond.StomachForce.User.domain;

import com.beyond.StomachForce.User.domain.Enum.VipGrade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class VipBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private VipGrade vipGrade;
    private String title;
    private String contents;
    private String benefitPhoto;


    public void updateImagePath(String imagePath){
        this.benefitPhoto = imagePath;
    }
}
