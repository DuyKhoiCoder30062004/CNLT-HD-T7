package com.example.ecommerce.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Thông tin đăng nhập không hợp lệ");
    }
}
