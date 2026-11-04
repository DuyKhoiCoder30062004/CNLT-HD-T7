package com.example.ecommerce.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VnPayCreateResponse {
    private Integer orderId;
    private Integer paymentId;
    private String transactionRef;
    private String status;
    private String paymentUrl;
}
