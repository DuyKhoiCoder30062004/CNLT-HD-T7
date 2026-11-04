
package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    Optional<Order> findByIdAndDeletedAtIsNull(Integer id);
    Optional<Order> findByPaymentId(Integer paymentId);
}
