package com.beyond.StomachForce.menu.dto;

import com.beyond.StomachForce.menu.domain.AllergyInfo;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MenuUpdateDto {
    private String name;
    private Long price;
    private String description;
    private String menuPhoto;
    private AllergyInfo allergyInfo;
}
