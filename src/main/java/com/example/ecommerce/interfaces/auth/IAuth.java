package com.example.ecommerce.interfaces.auth;

import com.example.ecommerce.dtos.auth.LoginRequest;
import com.example.ecommerce.dtos.auth.RegisterRequest;
import com.example.ecommerce.dtos.auth.LoginResponse;

public interface IAuth{
	LoginResponse register(RegisterRequest request);
	LoginResponse login(LoginRequest request);
	LoginResponse me(String token);
}
