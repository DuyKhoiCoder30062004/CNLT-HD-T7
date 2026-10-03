package com.example.ecommerce.entities;

import com.example.ecommerce.enums.Category;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique=true, length = 255)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "thumbnail")
    private String thumbnail;

    // gram
    @Column(name = "weight")
    private int weight;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount")
    private int discount;

    @Column(name = "category", columnDefinition = "bit")
    private Category category;

    @Column(name = "inventory_quantity", nullable = false)
    private int inventoryQuantity;

    @Column(name = "sold_quantity", nullable = false)
    private int soldQuantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(
        String name,
        int price,
        String thumbnail,
        int weight,
        String description,
        int discount,
        Category category,
        int inventoryQuantity
    ) {
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
        this.weight = weight;
        this.description = description;
        this.discount = discount;
        this.category = category;
        this.inventoryQuantity = inventoryQuantity;
        this.soldQuantity = 0;
        this.discount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}