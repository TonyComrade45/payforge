package com.payforge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
public class WalletResponse {

    private Long id;

    private BigDecimal balance;

    private String currency;

    private boolean active;

    public WalletResponse(
            Long id,
            BigDecimal balance,
            String currency,
            boolean active) {

        this.id = id;
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
        this.active = active;
    }
}