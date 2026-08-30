package com.payforge.dto.response;

import com.payforge.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AdminUserDetailsResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;

    private Long walletId;
    private BigDecimal balance;
    private String currency;
    private boolean walletActive;

    private long transactionCount;
}