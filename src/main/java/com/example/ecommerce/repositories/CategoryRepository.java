package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Category;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
	boolean existsByName(String name);

}