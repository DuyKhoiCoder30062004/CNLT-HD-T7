package com.example.ecommerce.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {

    private Integer id;

    private Integer productId;

    private String productName;

    private String imagePath;

    private Integer quantity;

    private Integer unitPrice;

    private Integer subtotal;
}
