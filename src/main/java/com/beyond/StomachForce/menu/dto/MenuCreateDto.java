package com.beyond.StomachForce.menu.dto;

import com.beyond.StomachForce.menu.domain.AllergyInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MenuCreateDto {
    @NotEmpty
    private Long restaurantId;

    @NotEmpty
    private String name;

    @NotEmpty
    private Long price;

    @NotEmpty
    private String description;

    private MultipartFile menuPhoto;

    private AllergyInfo allergyInfo;
}
