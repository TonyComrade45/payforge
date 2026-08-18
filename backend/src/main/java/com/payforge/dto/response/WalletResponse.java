package com.payforge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WalletResponse {

    private Long id;
    private BigDecimal balance;
    private String currency;
    private boolean active;
}