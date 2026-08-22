package com.payforge.controller;

import com.payforge.dto.response.TransactionResponse;
import com.payforge.entity.TransactionType;
import com.payforge.entity.User;
import com.payforge.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final WalletService walletService;

    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public Page<TransactionResponse> getTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return walletService.getTransactions(
                user.getId(),
                type,
                pageable
        );
    }
    @GetMapping("/{id}")
    public TransactionResponse getTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return walletService.getTransaction(id, user.getId());
    }
}