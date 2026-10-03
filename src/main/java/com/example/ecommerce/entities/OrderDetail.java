package com.example.ecommerce.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "order_details")
@Data
public class OrderDetail{

    @Id
    @Column(name = "id")
    private String id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable= false)
    private Product product;

    @OneToOne
    @JoinColumn(name = "order_id", nullable= false)
    private Order order;

    @Column(name="quantity", nullable=false)
    private int quantity;

    public OrderDetail(
    	Product product,
    	Order order,
        int quantity
    ){
    	this.product=product;
    	this.order=order;
        this.quantity=quantity;
    }
}