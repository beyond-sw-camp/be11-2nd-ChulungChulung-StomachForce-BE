package com.beyond.StomachForce.menu.dto;

import com.beyond.StomachForce.menu.domain.AllergyInfo;
import com.beyond.StomachForce.menu.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MenuListResDto {
    private Long id;
    private String name;
    private Long price;
    private String description;
    private String menuPhoto;
    private AllergyInfo allergyInfo;

    public MenuListResDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.menuPhoto = menu.getMenuPhoto();
        this.allergyInfo = menu.getAllergyInfo();
    }
}
