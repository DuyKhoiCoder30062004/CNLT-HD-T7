package com.example.ecommerce.dtos.product;

import com.example.ecommerce.dtos.receiver.ReceiverDTO;
import com.example.ecommerce.dtos.order.OrderDetailDTO;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Integer id;

    private Integer userId;

    private String username;

    private String status;

    private Integer totalAmount;

    private ReceiverDTO receiver;

    private List<OrderDetailDTO> products;

    private LocalDateTime createdAt;
}
