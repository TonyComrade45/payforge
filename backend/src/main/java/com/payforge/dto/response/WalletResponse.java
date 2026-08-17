package com.payforge.dto.response;

import java.math.BigDecimal;

public record WalletResponse(
        Long walletId,
        BigDecimal balance,
        String currency,
        boolean active
) {
}