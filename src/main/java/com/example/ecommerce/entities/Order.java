package com.example.ecommerce.entities;

import com.example.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "receiver_id", nullable= false)
    private Receiver receiver;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable= false)
    private User user;

    @Column(name = "status", columnDefinition = "bit")
    private OrderStatus status;

    @Column(name = "created_at", nullable= false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable= false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable= true)
    private LocalDateTime deletedAt;

    public Order(
    	Receiver receiver,
    	User user
    ){
    	this.receiver=receiver;
    	this.user=user;
    	this.status=OrderStatus.PENDING;
    	this.createdAt=LocalDateTime.now();
    	this.updatedAt=LocalDateTime.now();
    }
}