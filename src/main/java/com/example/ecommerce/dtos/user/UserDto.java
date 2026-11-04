package com.example.ecommerce.dtos.user;

import com.example.ecommerce.entities.Role;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}