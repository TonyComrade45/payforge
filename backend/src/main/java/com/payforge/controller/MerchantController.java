package com.payforge.controller;

import com.payforge.dto.response.AdminTransactionResponse;
import com.payforge.dto.response.WalletResponse;
import com.payforge.entity.User;
import com.payforge.service.MerchantService;
import com.payforge.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    private final WalletService walletService;
    private final MerchantService merchantService;

    public MerchantController(
            WalletService walletService,
            MerchantService merchantService) {

        this.walletService = walletService;
        this.merchantService = merchantService;
    }

    @GetMapping("/wallet")
    public WalletResponse getWallet(
            @AuthenticationPrincipal User merchant) {

        return walletService.getWallet(merchant);
    }

    @GetMapping("/transactions")
    public Page<AdminTransactionResponse> getTransactions(
            @AuthenticationPrincipal User merchant,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return merchantService.getTransactions(
                merchant,
                pageable
        );
    }
}