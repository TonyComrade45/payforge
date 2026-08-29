package com.payforge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalUsers;
    private long totalWallets;
    private long totalTransactions;
}