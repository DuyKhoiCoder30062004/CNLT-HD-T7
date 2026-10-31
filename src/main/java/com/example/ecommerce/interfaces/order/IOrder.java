package com.example.ecommerce.interfaces.order;

import com.example.ecommerce.dtos.order.OrderDTO;

import java.util.List;

public interface IOrder {
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Integer id);
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO updateOrderStatus(Integer id, String status);
    void deleteOrder(Integer id);
}
