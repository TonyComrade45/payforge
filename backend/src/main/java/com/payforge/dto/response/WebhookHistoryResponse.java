package com.payforge.dto.response;

import com.payforge.entity.WebhookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookHistoryResponse {

    private String eventId;

    private String eventType;

    private String paymentReferenceId;

    private String refundReferenceId;

    private BigDecimal amount;

    private WebhookStatus status;

    private int attempts;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime nextRetryAt;
}