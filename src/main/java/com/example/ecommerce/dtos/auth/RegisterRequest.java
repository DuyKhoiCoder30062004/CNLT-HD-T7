package com.example.ecommerce.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message="Tên đăng nhập không được để trống")
    @Size(min=5, max=20, message="Tên đăng nhập từ 2 đến 20 ký tự")
    private String username;

    @NotBlank(message="Mật khẩu không được để trống")
    @Size(min=8, max=20, message="Mật khẩu từ 8 đến 20 ký tự")
    private String password;

    @Email(message="Hãy cung cấp địa chỉ email hợp lệ")
    @NotBlank(message="Email không được để trống")
    private String email;

    @NotBlank(message="Số điện thoại không được để trống")
    @Pattern(
        regexp="^0\\d{9}$",
        message="Phải bắt đầu bằng 0 và đủ 10 số"
    )
    private String phone;

    public RegisterRequest(){}
    public RegisterRequest(
    	String username,
    	String password,
    	String email,
    	String phone
    ){
    	this.username = username;
    	this.password=password;
    	this.email=email;
    	this.phone=phone;
    }
}
