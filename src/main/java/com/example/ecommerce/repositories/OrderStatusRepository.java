package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.OrderStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {
	boolean existsByName(String name);

}