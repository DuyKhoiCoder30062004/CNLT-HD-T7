package com.example.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Integer quantity;

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;
    
    public OrderDetail(
            Product product,
            Order order,
            BigDecimal price,
            Integer quantity,
            BigDecimal totalPrice
    ) {
        this.product = product;
        this.order = order;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
}