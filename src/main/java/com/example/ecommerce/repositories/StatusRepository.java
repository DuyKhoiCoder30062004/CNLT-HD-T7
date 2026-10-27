package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Status;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
	boolean existsByName(String name);

}