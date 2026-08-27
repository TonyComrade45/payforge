package com.payforge.dto.response;

import com.payforge.entity.TransactionStatus;
import com.payforge.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;

    private String referenceId;

    private TransactionType type;

    private TransactionStatus status;

    private BigDecimal amount;

    private LocalDateTime createdAt;
}