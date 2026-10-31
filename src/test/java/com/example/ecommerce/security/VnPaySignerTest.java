package com.example.ecommerce.security;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VnPaySignerTest {

    @Test
    void shouldSortParametersAndCreateHmacSha512Signature() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("vnp_TxnRef", "123");
        parameters.put("vnp_Amount", "10000");

        assertEquals("vnp_Amount=10000&vnp_TxnRef=123", VnPaySigner.buildQuery(parameters));
        assertEquals(
                "d864d8660dee3399532771e5327feddd1a2d8e7baf8617f6d1420fdce9769584"
                        + "5b5f455aff23f8e4360c88bc13e588134c2fc0d421e5be8ac69e3b634d9548e4",
                VnPaySigner.sign(parameters, "secret")
        );
    }

    @Test
    void shouldIgnoreSecureHashWhenVerifyingCallback() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("vnp_TxnRef", "123");
        parameters.put("vnp_Amount", "10000");
        parameters.put("vnp_SecureHashType", "HmacSHA512");
        parameters.put("vnp_SecureHash", VnPaySigner.sign(parameters, "secret"));

        assertTrue(VnPaySigner.isValid(parameters, "secret"));
    }
}
