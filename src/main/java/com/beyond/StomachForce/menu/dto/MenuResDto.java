package com.beyond.StomachForce.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MenuResDto {
    private Long id;
    private Long price;
    private String description;
    private String menuPhoto;
}
