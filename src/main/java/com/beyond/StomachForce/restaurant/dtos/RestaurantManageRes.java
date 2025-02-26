package com.beyond.StomachForce.restaurant.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Getter
public class RestaurantManageRes {
    private Long id;
    private String status;
    private String email;
    private String phoneNumber;
    private String name;
}
