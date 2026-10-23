package dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ProductDTO {
    private Integer id;
    @NotBlank(message="Tên SP khong de trong'")
    private String name;
    @NotNull(message="gia san pham = 0")
    @Min(0)
    private Integer price;
    @NotBlank(message="Mo ta SP khong de trong")
    private String description;
    @Min(0)
    @NotBlank(message="giam gia san pham ve 0%")
    private Integer discount;
    @NotNull(message="so luong ton` kho khong duoc trong")
    private Integer inventoryQuantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(Integer inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @NotNull(message="Số lượng đã bán không được null")
    @Min(value = 0, message="Số lượng đã bán phải >= 0")
    private Integer soldQuantity;
    @NotBlank(message="Tên danh mục không được để trống")
    private String categoryName;

    @NotNull(message="Danh sách ảnh không được null")
    @NotEmpty(message="Danh sách ảnh phải có ít nhất một phần tử")
    private List<String> imagePaths;
}
