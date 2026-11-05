package com.example.ecommerce.controllers;

import com.example.ecommerce.dtos.product.ProductDTO;
import com.example.ecommerce.interfaces.product.IProduct;
import com.example.ecommerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProduct productService;

    @MockitoBean
    private JwtService jwtService;

    // 1. Test GET /api/v1/products (200 OK)
    @Test
    void shouldGetAllProducts() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setId(1);
        product.setName("Tai nghe Bluetooth");

        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Tai nghe Bluetooth"));
    }

    // 2. Test GET /api/v1/products/{id} (200 OK)
    @Test
    void shouldGetProductById() throws Exception {
        ProductDTO response = new ProductDTO();
        response.setId(1);
        response.setName("Điện thoại thông minh");
        response.setPrice(15000000);

        when(productService.getProductById(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Điện thoại thông minh"))
                .andExpect(jsonPath("$.price").value(15000000));
    }

    // 3. Test POST /api/v1/products (201 Created)
    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        ProductDTO response = new ProductDTO();
        response.setId(10);
        response.setName("Đồng hồ thông minh");
        response.setPrice(2500000);

        when(productService.createProduct(any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Đồng hồ thông minh",
                                  "price": 2500000,
                                  "description": "Đồng hồ đo nhịp tim, chống nước",
                                  "discount": 10,
                                  "inventoryQuantity": 100,
                                  "soldQuantity": 0,
                                  "categoryName": "WATCH",
                                  "imagePaths": ["/imgs/products/watch-1.jpg"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Đồng hồ thông minh"));
    }

    // 4. Test POST vi phạm Bean Validation (400 Bad Request)
    @Test
    void shouldRejectCreateProductWhenInputIsInvalid() throws Exception {
        // Gửi body sai: tên để trống, giá âm, danh sách ảnh rỗng
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "price": -50000,
                                  "description": "",
                                  "discount": -5,
                                  "inventoryQuantity": -1,
                                  "soldQuantity": 0,
                                  "categoryName": "",
                                  "imagePaths": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists());
    }

    // 5. Test PUT /api/v1/products/{id} (200 OK)
    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductDTO response = new ProductDTO();
        response.setId(1);
        response.setName("Laptop Gaming (Đã cập nhật)");
        response.setPrice(22000000);

        when(productService.updateProduct(eq(1), any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Laptop Gaming (Đã cập nhật)",
                                  "price": 22000000,
                                  "description": "Cấu hình nâng cấp RAM 32GB",
                                  "discount": 5,
                                  "inventoryQuantity": 30,
                                  "soldQuantity": 5,
                                  "categoryName": "LAPTOP",
                                  "imagePaths": ["/imgs/products/laptop.jpg"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Gaming (Đã cập nhật)"))
                .andExpect(jsonPath("$.price").value(22000000));
    }

    // 6. Test DELETE /api/v1/products/{id} (204 No Content)
    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        doNothing().when(productService).deleteProduct(1);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }
}
