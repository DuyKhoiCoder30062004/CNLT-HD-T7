package com.example.ecommerce.dtos.auth;

import com.example.ecommerce.dtos.user.UserDto;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;

    public LoginResponse(){}
    public LoginResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }

}
