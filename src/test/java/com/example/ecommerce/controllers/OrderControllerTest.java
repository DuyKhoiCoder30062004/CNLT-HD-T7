package com.example.ecommerce.controllers;

import com.example.ecommerce.dtos.order.OrderDTO;
import com.example.ecommerce.interfaces.order.IOrder;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOrder orderService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(new OrderDTO()));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        OrderDTO response = new OrderDTO();
        response.setId(1);
        when(orderService.getOrderById(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldCreateOrder() throws Exception {
        OrderDTO response = new OrderDTO();
        response.setId(4);
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "receiver": {"id": 1},
                                  "products": [{"productId": 1, "quantity": 2}]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void shouldRejectOrderWithoutProducts() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "receiver": {"id": 1},
                                  "products": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.products").exists());
    }

    @Test
    void shouldUpdateOrderStatus() throws Exception {
        OrderDTO response = new OrderDTO();
        response.setId(1);
        response.setStatus("CONFIRMED");
        when(orderService.updateOrderStatus(eq(1), eq("CONFIRMED"))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "CONFIRMED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1);

        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isNoContent());
    }
}
