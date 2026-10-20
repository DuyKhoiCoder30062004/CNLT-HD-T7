package com.example.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "receivers")
@Data
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "hamlet", nullable = false)
    private String hamlet;

    @Column(name = "ward", nullable = false)
    private String ward;

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Receiver() {}
    public Receiver(
        String fullname,
        String phone,
        String hamlet,
        String ward,
        String province     
    ) {
        this.fullname = fullname;
        this.phone = phone;
        this.hamlet = hamlet;
        this.ward = ward;
        this.province = province;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}