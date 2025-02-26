package com.beyond.StomachForce.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MenuOrderReq {

    private Long menuId;
    private Integer quantity;
}
