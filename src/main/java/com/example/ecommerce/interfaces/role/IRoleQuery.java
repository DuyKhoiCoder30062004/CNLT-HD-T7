package com.example.ecommerce.interfaces.role;

import com.example.ecommerce.entities.Role;
import org.springframework.data.domain.Page;
import java.util.List;

public interface IRoleQuery {
    boolean existsByName(String username);
    Role findByUser();
}