package com.beyond.StomachForce.User.dtos;

import com.beyond.StomachForce.User.domain.Enum.EarnedMileage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor //기본생성자
@AllArgsConstructor //모든 매개변수있는 생성자
@Data
@Builder
public class ManageMileageDto {
    private EarnedMileage earnedMileage;
    private Long mileageAmount;
}
