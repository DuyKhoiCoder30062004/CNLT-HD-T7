package com.example.ecommerce.interfaces.user;

import com.example.ecommerce.entities.User;
import org.springframework.data.domain.Page;
import java.util.List;

public interface IUserQuery {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findByPhone(String phone);
}