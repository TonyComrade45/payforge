package com.payforge.controller;

import com.payforge.dto.response.WalletResponse;
import com.payforge.entity.User;
import com.payforge.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public WalletResponse getWallet(
            @AuthenticationPrincipal User user) {

        return walletService.getWallet(user.getId());
    }
}