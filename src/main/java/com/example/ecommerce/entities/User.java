package com.example.ecommerce.entities;

import com.example.ecommerce.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username",nullable= false, unique=true, length=50)
    private String username;

    @Column( name = "email", nullable= false, unique=true,length=50)
    private String email;

    @Column( name = "phone",nullable= false, unique=true, length=10)
    private String phone;

    @Column( name = "password", nullable= false, length=255 )
    private String password;

    @Column( name = "avatar", nullable= true, length=50 )
    private String avatar;

    @Column( name = "role_id", columnDefinition="bit" )
    private Role role;

    @Column( name = "created_at", nullable= false )
    private LocalDateTime createdAt;

    @Column( name = "updated_at", nullable= false )
    private LocalDateTime updatedAt;

    @Column( name = "deleted_at" )
    private LocalDateTime deletedAt;

    public User(){}
    public User(
        String username,
        String email,
        String phone,
        String password
    ){
        this.username = username;
        this.email=email;
        this.phone=phone;
        this.password=password;
        this.role=Role.USER;
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
    }

}