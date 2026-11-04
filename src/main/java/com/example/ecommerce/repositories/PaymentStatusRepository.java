package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.PaymentStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {
	boolean existsByName(String name);
	Optional<PaymentStatus> findByNameIgnoreCase(String name);

}
