package com.example.ecommerce.interfaces.payment;

import com.example.ecommerce.dtos.payment.VnPayCreateRequest;
import com.example.ecommerce.dtos.payment.VnPayCreateResponse;
import com.example.ecommerce.dtos.payment.VnPayIpnResponse;
import com.example.ecommerce.dtos.payment.VnPayResultResponse;

import java.util.Map;

public interface IVnPay {
    VnPayCreateResponse createPayment(VnPayCreateRequest request, String clientIp);
    VnPayResultResponse processReturn(Map<String, String> parameters);
    VnPayIpnResponse processIpn(Map<String, String> parameters);
}
