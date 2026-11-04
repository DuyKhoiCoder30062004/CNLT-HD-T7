package com.example.ecommerce.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class VnPaySigner {

    private static final String HMAC_SHA_512 = "HmacSHA512";

    private VnPaySigner() {
    }

    public static String buildQuery(Map<String, String> parameters) {
        return sortedPaymentParameters(parameters).entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    public static String sign(Map<String, String> parameters, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_512);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_512));
            byte[] digest = mac.doFinal(buildQuery(parameters).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                hex.append(String.format("%02x", value & 0xff));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể tạo chữ ký VNPay", ex);
        }
    }

    public static boolean isValid(Map<String, String> parameters, String secret) {
        String receivedHash = parameters.get("vnp_SecureHash");
        return receivedHash != null && receivedHash.equalsIgnoreCase(sign(parameters, secret));
    }

    private static Map<String, String> sortedPaymentParameters(Map<String, String> parameters) {
        Map<String, String> sorted = new TreeMap<>();
        parameters.forEach((key, value) -> {
            if (key.startsWith("vnp_")
                    && !key.equals("vnp_SecureHash")
                    && !key.equals("vnp_SecureHashType")
                    && value != null
                    && !value.isBlank()) {
                sorted.put(key, value);
            }
        });
        return sorted;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
