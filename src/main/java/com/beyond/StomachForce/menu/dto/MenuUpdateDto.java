package com.beyond.StomachForce.menu.dto;

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
}
