package com.example.ecommerce.mappers;

import com.example.ecommerce.dtos.auth.LoginResponse;
import com.example.ecommerce.dtos.user.UserDto;
import com.example.ecommerce.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    private final ModelMapper modelMapper;

    public AuthMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public LoginResponse toLoginResponse(String token, User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class); 
        return new LoginResponse(token, userDto);
    }
}
