package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Status;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
	boolean existsByName(String name);
	Optional<Status> findByNameIgnoreCase(String name);

}
