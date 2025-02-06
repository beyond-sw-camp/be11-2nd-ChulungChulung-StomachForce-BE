package com.beyond.StomachForce.youngjae.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {
    private String registrationNumber;
    private String password;
}
