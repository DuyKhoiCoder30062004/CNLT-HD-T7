package com.example.ecommerce.controllers;

import com.example.ecommerce.dtos.auth.LoginRequest;
import com.example.ecommerce.dtos.auth.LoginResponse;
import com.example.ecommerce.dtos.auth.RegisterRequest;
import com.example.ecommerce.interfaces.auth.IAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final IAuth authService;

	public AuthController(IAuth authService){
		this.authService=authService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
		@Valid
		@RequestBody LoginRequest request){
		LoginResponse result = authService.login(request);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/me")
	public ResponseEntity<LoginResponse> me(
		@RequestHeader("Authorization")
		String bearerToken
	) {
	    String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
	    
	    LoginResponse result = authService.me(token);
	    return ResponseEntity.ok(result);
	}

	@PostMapping("/register")
	public ResponseEntity<LoginResponse> register(
		@Valid
		@RequestBody RegisterRequest request){
		LoginResponse result = authService.register(request);
		return ResponseEntity.ok(result);
	}
}
