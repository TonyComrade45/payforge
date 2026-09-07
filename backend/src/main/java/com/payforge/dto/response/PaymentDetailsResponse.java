package com.payforge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsResponse {

    private String paymentReferenceId;

    private String customerEmail;

    private String merchantEmail;

    private BigDecimal amount;

    private BigDecimal refundedAmount;

    private BigDecimal remainingRefundableAmount;

    private String status;

    private LocalDateTime createdAt;
}