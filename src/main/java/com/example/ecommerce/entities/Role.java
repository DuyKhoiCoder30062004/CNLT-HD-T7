package com.example.ecommerce.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name",  nullable=false)
    private String name;

    @Column(name = "description",  nullable=false)
    private String description;

    public Role(
    	String name,
    	String description
    ){
    	this.name=name;
    	this.description=description;
    }
}