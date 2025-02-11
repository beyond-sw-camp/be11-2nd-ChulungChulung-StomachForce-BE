package com.beyond.StomachForce.menu.dto;

import com.beyond.StomachForce.allergyInfo.domain.AllergyInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MenuCreateDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private Long price;

    @NotEmpty
    private String description;

    @NotEmpty
    private String menuPhoto;

    private AllergyInfo allergyInfo;
}
