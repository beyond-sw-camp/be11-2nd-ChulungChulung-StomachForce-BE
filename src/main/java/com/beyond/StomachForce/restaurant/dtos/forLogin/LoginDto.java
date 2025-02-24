package com.beyond.StomachForce.restaurant.dtos.forLogin;

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
