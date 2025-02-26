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
public class MenuResDto {
    private Long id;
    private String name;
    private Long price;
    private String description;
    private String menuPhoto;
    private Long restaurantId;
    private AllergyInfo allergyInfo;
    private Integer quantity;

    public MenuResDto(Menu menu){
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.menuPhoto = menu.getMenuPhoto();
        this.restaurantId = menu.getRestaurant().getId();
        this.allergyInfo = menu.getAllergyInfo();
    }
}
