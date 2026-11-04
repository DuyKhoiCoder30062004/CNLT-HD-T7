package com.example.ecommerce.services.payment;

import com.example.ecommerce.config.VnPayConfig;
import com.example.ecommerce.dtos.payment.VnPayCreateRequest;
import com.example.ecommerce.dtos.payment.VnPayCreateResponse;
import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.Payment;
import com.example.ecommerce.entities.PaymentStatus;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.repositories.PaymentRepository;
import com.example.ecommerce.repositories.PaymentStatusRepository;
import com.example.ecommerce.repositories.StatusRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VnPayServiceTest {

    @Test
    void shouldCreateSignedPaymentUrlForExistingOrder() {
        VnPayConfig config = mock(VnPayConfig.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        PaymentStatusRepository paymentStatusRepository = mock(PaymentStatusRepository.class);
        StatusRepository statusRepository = mock(StatusRepository.class);

        when(config.getPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        when(config.getReturnUrl()).thenReturn("http://localhost:8080/api/v1/payments/vnpay/return");
        when(config.getTmnCode()).thenReturn("TESTCODE");
        when(config.getHashSecret()).thenReturn("test-secret");

        Order order = new Order();
        order.setId(4);
        order.setTotalPrice(BigDecimal.valueOf(100000));
        when(orderRepository.findByIdAndDeletedAtIsNull(4)).thenReturn(Optional.of(order));

        PaymentStatus unpaid = new PaymentStatus("UNPAID", "Chưa thanh toán");
        unpaid.setId(1);
        when(paymentStatusRepository.findByNameIgnoreCase("UNPAID")).thenReturn(Optional.of(unpaid));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(10);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VnPayService service = new VnPayService(
                config,
                orderRepository,
                paymentRepository,
                paymentStatusRepository,
                statusRepository
        );
        VnPayCreateRequest request = new VnPayCreateRequest();
        request.setOrderId(4);

        VnPayCreateResponse response = service.createPayment(request, "127.0.0.1");

        assertEquals(4, response.getOrderId());
        assertEquals(10, response.getPaymentId());
        assertEquals("UNPAID", response.getStatus());
        assertTrue(response.getPaymentUrl().contains("vnp_Amount=10000000"));
        assertTrue(response.getPaymentUrl().contains("vnp_TmnCode=TESTCODE"));
        assertTrue(response.getPaymentUrl().contains("vnp_SecureHash="));
    }
}
