package com.beyond.StomachForce.Common.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatusCode {
    private int status_code;
    private String status_message;
    private Object result;
}
