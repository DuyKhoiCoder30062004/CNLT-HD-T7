package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Role;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
	boolean existsByName(String name);
	Role findByName(String name);

	default Role findByUser(){
		Role userRole = findByName("USER");
		return userRole;
	}
	default Role findByStaff(){
		Role userRole = findByName("STAFF");
		return userRole;
	}
	default Role findByAdmin(){
		Role userRole = findByName("ADMIN");
		return userRole;
	}

}