package entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer price;
    private String thumbnail;
    private Integer weight;
    private String description;
    private Integer discount;
    private Integer totalPrice;
    private Integer inventoryQuantity;
    private Integer soldQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime editedAt;
    private Boolean isDeleted;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Image> images;

}
