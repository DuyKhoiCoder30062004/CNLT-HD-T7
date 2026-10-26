package com.example.ecommerce.services.auth;

import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.InvalidCredentialsException;
import com.example.ecommerce.mappers.AuthMapper;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.dtos.auth.LoginRequest;
import com.example.ecommerce.dtos.auth.LoginResponse;
import com.example.ecommerce.dtos.auth.RegisterRequest;
import com.example.ecommerce.interfaces.auth.IAuth;
import com.example.ecommerce.interfaces.user.IUserCreate;
import com.example.ecommerce.interfaces.user.IUserQuery;
import com.example.ecommerce.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

@Service 
public class AuthService implements IAuth{
	private final IUserQuery userQueryService;
	private final IUserCreate userCreateService;
	private final JwtService jwtService;
	private final AuthMapper authMapper;

	public AuthService(
		IUserQuery userQueryService,
		IUserCreate userCreateService,
		JwtService jwtService,
		AuthMapper authMapper
	){
		this.userQueryService=userQueryService;
		this.userCreateService = userCreateService;
		this.jwtService = jwtService;
		this.authMapper= authMapper;
	}

	@Override
	public LoginResponse register(RegisterRequest request){

		if(userQueryService.existsByUsername(request.getUsername())){
			throw new DuplicateResourceException("Tên tài khoản đã tồn tại. Vui lòng thử tên khác");
		}

		if(userQueryService.existsByEmail(request.getEmail())){
			throw new DuplicateResourceException("Email đã tồn tại. Vui lòng thử email khác");
		}

		User user = userCreateService.create(request);
		LoginRequest loginRequest = new LoginRequest(
			request.getPhone(),
			request.getPassword()
		);
		return this.login(loginRequest);
	}

	@Override
	public LoginResponse login(LoginRequest request){
		User user = userQueryService.findByPhone(request.getPhone());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		boolean isPassword = encoder.matches(request.getPassword(), user.getPassword());
		if(!isPassword){
			throw new InvalidCredentialsException();
		}
		String token = jwtService.generateToken(request.getPhone());
		LoginResponse response = authMapper.toLoginResponse(token, user);
		return response;
	}

	@Override
	public LoginResponse me(String token){
		String phone = SecurityContextHolder.getContext().getAuthentication().getName();
	    User user = userQueryService.findByPhone(phone);
		LoginResponse response = authMapper.toLoginResponse(token, user);
		return response;
	}

}
