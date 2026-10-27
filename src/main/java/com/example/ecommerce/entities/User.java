package com.example.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String username;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(length = 500)
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    public User(
            String username,
            String phone,
            String email,
            String password,
            Role role
    ) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}