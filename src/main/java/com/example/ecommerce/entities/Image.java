package com.example.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "images")
@Data
public class Image{

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable= false)
    private Product product;

    @Column(name = "path",nullable= false)
    private String path;

    @Column(name = "alt", nullable= false, length=50)
    private String alt;

    public Image(
    	Product product,
    	String path,
    	String alt
    ){
    	this.product=product;
    	this.path=path;
    	this.alt=alt;
    }
}