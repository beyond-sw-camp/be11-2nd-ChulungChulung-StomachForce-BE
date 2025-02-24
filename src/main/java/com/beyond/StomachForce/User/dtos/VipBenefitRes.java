package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.VipGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VipBenefitRes {
    private VipGrade vipGrade;
    private String title;
    private String contents;
    private String benefitPhoto;
}
