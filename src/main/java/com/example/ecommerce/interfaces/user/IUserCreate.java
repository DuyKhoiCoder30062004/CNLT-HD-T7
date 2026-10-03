package com.example.ecommerce.interfaces.user;

import com.example.ecommerce.dtos.auth.RegisterRequest;
import com.example.ecommerce.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface IUserCreate {
    User create(RegisterRequest request);

    default String hashPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        return hashedPassword;
    }
}