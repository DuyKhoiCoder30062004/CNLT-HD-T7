package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.PaymentStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, String> {
	boolean existsByName(String name);

}