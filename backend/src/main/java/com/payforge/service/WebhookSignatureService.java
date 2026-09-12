package com.payforge.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class WebhookSignatureService {

    public String generateSignature(
            String payload,
            String secret) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            secret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            mac.init(secretKey);

            byte[] hash =
                    mac.doFinal(
                            payload.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {
                hexString.append(
                        String.format("%02x", b)
                );
            }

            return hexString.toString();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to generate webhook signature",
                    exception
            );
        }
    }
}