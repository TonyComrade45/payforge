package com.payforge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload {

    private String eventId;

    private String eventType;

    private String paymentReferenceId;

    private String refundReferenceId;

    private BigDecimal amount;

    private LocalDateTime createdAt;
}