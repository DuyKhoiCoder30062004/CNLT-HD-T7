package com.example.ecommerce.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VnPayResultResponse {
    private boolean validSignature;
    private boolean successful;
    private Integer orderId;
    private String transactionRef;
    private String transactionNo;
    private String responseCode;
    private String paymentStatus;
    private String message;
}
