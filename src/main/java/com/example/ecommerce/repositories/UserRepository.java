package com.example.ecommerce.repositories;

import com.example.ecommerce.dtos.auth.RegisterRequest;
import com.example.ecommerce.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByUsername(String username);
	boolean existsById(String userId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
	User findByPhone(String phone);

}