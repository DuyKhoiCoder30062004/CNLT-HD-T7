package com.example.ecommerce.controllers;

import com.example.ecommerce.dtos.payment.VnPayCreateResponse;
import com.example.ecommerce.dtos.payment.VnPayIpnResponse;
import com.example.ecommerce.dtos.payment.VnPayResultResponse;
import com.example.ecommerce.interfaces.payment.IVnPay;
import com.example.ecommerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VnPayController.class)
class VnPayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IVnPay vnPayService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldCreateVnPayPayment() throws Exception {
        when(vnPayService.createPayment(any(), anyString())).thenReturn(
                new VnPayCreateResponse(4, 10, "4123", "UNPAID", "https://sandbox.vnpayment.vn/pay")
        );

        mockMvc.perform(post("/api/v1/payments/vnpay/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId": 4, "locale": "vn"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(4))
                .andExpect(jsonPath("$.status").value("UNPAID"));
    }

    @Test
    void shouldProcessVnPayReturn() throws Exception {
        when(vnPayService.processReturn(any())).thenReturn(
                new VnPayResultResponse(true, true, 4, "4123", "99", "00", "PAID", "Thanh toán thành công")
        );

        mockMvc.perform(get("/api/v1/payments/vnpay/return")
                        .param("vnp_TxnRef", "4123")
                        .param("vnp_SecureHash", "hash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    void shouldProcessVnPayIpn() throws Exception {
        when(vnPayService.processIpn(any())).thenReturn(new VnPayIpnResponse("00", "Confirm Success"));

        mockMvc.perform(get("/api/v1/payments/vnpay/ipn")
                        .param("vnp_TxnRef", "4123")
                        .param("vnp_SecureHash", "hash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("00"))
                .andExpect(jsonPath("$.Message").value("Confirm Success"));
    }
}
