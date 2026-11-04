package com.example.ecommerce.dtos.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {

    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    private Integer price;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    private String description;

    @NotNull(message = "Giảm giá không được để trống")
    @Min(value = 0, message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
    private Integer discount;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer inventoryQuantity;

    @NotNull(message = "Số lượng đã bán không được để trống")
    @Min(value = 0, message = "Số lượng đã bán phải lớn hơn hoặc bằng 0")
    private Integer soldQuantity;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;

    @NotNull(message = "Danh sách ảnh không được để trống")
    @NotEmpty(message = "Danh sách ảnh phải có ít nhất một ảnh")
    private List<String> imagePaths;
}