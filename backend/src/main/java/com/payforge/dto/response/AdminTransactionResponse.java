package com.payforge.dto.response;

import com.payforge.entity.TransactionStatus;
import com.payforge.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminTransactionResponse {

    private Long id;

    private Long userId;
    private String userEmail;

    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private String referenceId;
    private LocalDateTime createdAt;
}