package com.payforge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundHistoryResponse {

    private String referenceId;

    private String paymentReferenceId;

    private String merchantEmail;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;
}