package com.example.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255)
    private String name;

    private Integer price;

    @Column(length = 500)
    private String thumbnail;

    private Integer weight;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer discount;

    @Column(name = "total_price")
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "inventory_quantity")
    private Integer inventoryQuantity;

    @Column(name = "sold_quantity")
    private Integer soldQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "product")
    private List<Image> images = new ArrayList<>();

    public Product(
        String name,
        Integer price,
        String thumbnail,
        Integer weight,
        String description,
        Integer discount,
        Integer totalPrice,
        Category category,
        Integer inventoryQuantity,
        Integer soldQuantity
    ) {
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
        this.weight = weight;
        this.description = description;
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.category = category;
        this.inventoryQuantity = inventoryQuantity;
        this.soldQuantity = soldQuantity;
        this.isDeleted = false;
    }
}