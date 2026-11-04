package com.example.ecommerce.services.payment;

import com.example.ecommerce.config.VnPayConfig;
import com.example.ecommerce.dtos.payment.VnPayCreateRequest;
import com.example.ecommerce.dtos.payment.VnPayCreateResponse;
import com.example.ecommerce.dtos.payment.VnPayIpnResponse;
import com.example.ecommerce.dtos.payment.VnPayResultResponse;
import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.Payment;
import com.example.ecommerce.entities.PaymentStatus;
import com.example.ecommerce.entities.Status;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.interfaces.payment.IVnPay;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.repositories.PaymentRepository;
import com.example.ecommerce.repositories.PaymentStatusRepository;
import com.example.ecommerce.repositories.StatusRepository;
import com.example.ecommerce.security.VnPaySigner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class VnPayService implements IVnPay {

    private static final ZoneId VN_TIME_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final BigDecimal VNPAY_AMOUNT_MULTIPLIER = BigDecimal.valueOf(100);

    private final VnPayConfig config;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final StatusRepository statusRepository;

    public VnPayService(
            VnPayConfig config,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            PaymentStatusRepository paymentStatusRepository,
            StatusRepository statusRepository
    ) {
        this.config = config;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentStatusRepository = paymentStatusRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    @Transactional
    public VnPayCreateResponse createPayment(VnPayCreateRequest request, String clientIp) {
        validateConfiguration();
        String locale = normalizeLocale(request.getLocale());
        Order order = orderRepository.findByIdAndDeletedAtIsNull(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (order.getPayment() != null) {
            throw new IllegalArgumentException("Đơn hàng đã có phương thức thanh toán");
        }
        if (order.getTotalPrice() == null || order.getTotalPrice().compareTo(BigDecimal.valueOf(5000)) < 0) {
            throw new IllegalArgumentException("VNPay yêu cầu số tiền thanh toán tối thiểu là 5.000 VND");
        }

        String transactionRef = order.getId() + String.valueOf(System.currentTimeMillis());
        Payment payment = new Payment(
                findPaymentStatus("UNPAID"),
                "VNPAY",
                order.getTotalPrice().intValueExact(),
                transactionRef
        );
        payment.setCreatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);
        order.setEditedAt(LocalDateTime.now());
        orderRepository.save(order);

        Map<String, String> parameters = createParameters(order, transactionRef, clientIp, locale);
        String signature = VnPaySigner.sign(parameters, config.getHashSecret());
        String paymentUrl = config.getPayUrl()
                + "?" + VnPaySigner.buildQuery(parameters)
                + "&vnp_SecureHash=" + signature;

        return new VnPayCreateResponse(
                order.getId(),
                savedPayment.getId(),
                transactionRef,
                savedPayment.getPaymentStatus().getName(),
                paymentUrl
        );
    }

    @Override
    @Transactional
    public VnPayResultResponse processReturn(Map<String, String> parameters) {
        validateConfiguration();
        if (!VnPaySigner.isValid(parameters, config.getHashSecret())) {
            return result(parameters, null, false, false, "Chữ ký VNPay không hợp lệ");
        }

        Payment payment = findPayment(parameters.get("vnp_TxnRef"));
        Order order = findOrder(payment);
        if (!hasValidAmount(parameters, payment)) {
            return result(parameters, order, true, false, "Số tiền thanh toán không hợp lệ");
        }

        boolean successful = isSuccessful(parameters);
        updatePaymentResult(payment, order, successful);
        return result(
                parameters,
                order,
                true,
                successful,
                successful ? "Thanh toán thành công" : "Thanh toán không thành công"
        );
    }

    @Override
    @Transactional
    public VnPayIpnResponse processIpn(Map<String, String> parameters) {
        validateConfiguration();
        if (!VnPaySigner.isValid(parameters, config.getHashSecret())) {
            return new VnPayIpnResponse("97", "Invalid Checksum");
        }

        Payment payment = paymentRepository.findByTransactionId(parameters.get("vnp_TxnRef")).orElse(null);
        if (payment == null) {
            return new VnPayIpnResponse("01", "Order not Found");
        }
        if (!hasValidAmount(parameters, payment)) {
            return new VnPayIpnResponse("04", "Invalid Amount");
        }
        if ("PAID".equalsIgnoreCase(payment.getPaymentStatus().getName())) {
            return new VnPayIpnResponse("02", "Order already Confirmed");
        }

        Order order = findOrder(payment);
        updatePaymentResult(payment, order, isSuccessful(parameters));
        return new VnPayIpnResponse("00", "Confirm Success");
    }

    private Map<String, String> createParameters(
            Order order,
            String transactionRef,
            String clientIp,
            String locale
    ) {
        ZonedDateTime now = ZonedDateTime.now(VN_TIME_ZONE);
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("vnp_Version", "2.1.0");
        parameters.put("vnp_Command", "pay");
        parameters.put("vnp_TmnCode", config.getTmnCode());
        parameters.put("vnp_Amount", order.getTotalPrice().multiply(VNPAY_AMOUNT_MULTIPLIER).toBigIntegerExact().toString());
        parameters.put("vnp_CurrCode", "VND");
        parameters.put("vnp_TxnRef", transactionRef);
        parameters.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        parameters.put("vnp_OrderType", "other");
        parameters.put("vnp_Locale", locale);
        parameters.put("vnp_ReturnUrl", config.getReturnUrl());
        parameters.put("vnp_IpAddr", clientIp == null || clientIp.isBlank() ? "127.0.0.1" : clientIp);
        parameters.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        parameters.put("vnp_ExpireDate", now.plusMinutes(15).format(VNPAY_DATE_FORMAT));
        return parameters;
    }

    private void updatePaymentResult(Payment payment, Order order, boolean successful) {
        String currentStatus = payment.getPaymentStatus().getName();
        if ("PAID".equalsIgnoreCase(currentStatus)) {
            return;
        }

        payment.setPaymentStatus(findPaymentStatus(successful ? "PAID" : "FAILED"));
        paymentRepository.save(payment);

        if (successful && "PENDING".equalsIgnoreCase(order.getStatus().getName())) {
            Status confirmed = statusRepository.findByNameIgnoreCase("CONFIRMED")
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái CONFIRMED"));
            order.setStatus(confirmed);
            order.setEditedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    private Payment findPayment(String transactionRef) {
        return paymentRepository.findByTransactionId(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch thanh toán"));
    }

    private Order findOrder(Payment payment) {
        return orderRepository.findByPaymentId(payment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng của giao dịch"));
    }

    private PaymentStatus findPaymentStatus(String name) {
        return paymentStatusRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái thanh toán " + name));
    }

    private boolean hasValidAmount(Map<String, String> parameters, Payment payment) {
        try {
            long expectedAmount = Math.multiplyExact(payment.getAmount().longValue(), 100L);
            return expectedAmount == Long.parseLong(parameters.get("vnp_Amount"));
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private boolean isSuccessful(Map<String, String> parameters) {
        return "00".equals(parameters.get("vnp_ResponseCode"))
                && "00".equals(parameters.get("vnp_TransactionStatus"));
    }

    private VnPayResultResponse result(
            Map<String, String> parameters,
            Order order,
            boolean validSignature,
            boolean successful,
            String message
    ) {
        return new VnPayResultResponse(
                validSignature,
                successful,
                order == null ? null : order.getId(),
                parameters.get("vnp_TxnRef"),
                parameters.get("vnp_TransactionNo"),
                parameters.get("vnp_ResponseCode"),
                order == null || order.getPayment() == null
                        ? null
                        : order.getPayment().getPaymentStatus().getName(),
                message
        );
    }

    private String normalizeLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            return "vn";
        }
        if (!locale.equals("vn") && !locale.equals("en")) {
            throw new IllegalArgumentException("Ngôn ngữ VNPay chỉ hỗ trợ vn hoặc en");
        }
        return locale;
    }

    private void validateConfiguration() {
        if (config.getTmnCode() == null || config.getTmnCode().isBlank()
                || config.getHashSecret() == null || config.getHashSecret().isBlank()) {
            throw new IllegalArgumentException("Chưa cấu hình VNPAY_TMN_CODE và VNPAY_HASH_SECRET");
        }
    }
}
