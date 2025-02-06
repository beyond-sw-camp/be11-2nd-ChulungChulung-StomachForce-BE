package com.beyond.StomachForce.User.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor //기본생성자
@AllArgsConstructor //모든 매개변수있는 생성자
@Data
public class LoginDto {
    private String identify;
    private String password;
}
