package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRes {
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
}