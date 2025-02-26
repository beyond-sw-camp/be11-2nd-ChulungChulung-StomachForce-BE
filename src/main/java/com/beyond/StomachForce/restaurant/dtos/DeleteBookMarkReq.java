package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteBookMarkReq {
    private Long restaurantId;
}
