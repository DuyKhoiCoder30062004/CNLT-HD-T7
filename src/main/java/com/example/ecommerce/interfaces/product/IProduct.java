package com.example.ecommerce.interfaces.product;

import com.example.ecommerce.dtos.product.ProductDTO;
import java.util.List;

public interface IProduct {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Integer id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Integer id, ProductDTO productDTO);
    void deleteProduct(Integer id);
}

