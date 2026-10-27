package com.example.ecommerce.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message="Số điện thoại không được để trống")
    @Pattern(
        regexp="^0\\d{9}$",
        message="Phải bắt đầu bằng 0 và đủ 10 số"
    )
    private String phone;

    @NotBlank(message="Mật khẩu không được để trống")
    @Size(min=8, max=20, message="Mật khẩu từ 8 đến 20 ký tự")
    private String password;

    public LoginRequest(){}
    public LoginRequest(String phone, String password){
        this.phone=phone;
        this.password=password;
    }
}
