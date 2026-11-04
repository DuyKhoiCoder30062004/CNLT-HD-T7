package com.example.ecommerce.controllers;

import com.example.ecommerce.dtos.payment.VnPayCreateRequest;
import com.example.ecommerce.dtos.payment.VnPayCreateResponse;
import com.example.ecommerce.dtos.payment.VnPayIpnResponse;
import com.example.ecommerce.dtos.payment.VnPayResultResponse;
import com.example.ecommerce.interfaces.payment.IVnPay;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/vnpay")
public class VnPayController {

    private final IVnPay vnPayService;

    public VnPayController(IVnPay vnPayService) {
        this.vnPayService = vnPayService;
    }

    @PostMapping("/create")
    public ResponseEntity<VnPayCreateResponse> createPayment(
            @Valid @RequestBody VnPayCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vnPayService.createPayment(request, getClientIp(servletRequest)));
    }

    @GetMapping("/return")
    public VnPayResultResponse paymentReturn(@RequestParam Map<String, String> parameters) {
        return vnPayService.processReturn(parameters);
    }

    @GetMapping("/ipn")
    public VnPayIpnResponse paymentIpn(@RequestParam Map<String, String> parameters) {
        return vnPayService.processIpn(parameters);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
