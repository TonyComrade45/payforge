package com.payforge.controller;

import com.payforge.dto.response.TransactionResponse;
import com.payforge.entity.User;
import com.payforge.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final WalletService walletService;

    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<TransactionResponse> getTransactions(
            @AuthenticationPrincipal User user) {

        return walletService.getTransactions(user.getId());
    }
    @GetMapping("/{id}")
    public TransactionResponse getTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return walletService.getTransaction(id, user.getId());
    }
}