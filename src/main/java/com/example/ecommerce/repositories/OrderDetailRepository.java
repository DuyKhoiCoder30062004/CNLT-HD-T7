package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findAllByOrderIdOrderById(Integer orderId);
}
