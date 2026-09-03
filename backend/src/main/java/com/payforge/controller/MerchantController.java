package com.payforge.controller;

import com.payforge.dto.response.WalletResponse;
import com.payforge.entity.User;
import com.payforge.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    private final WalletService walletService;

    public MerchantController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallet")
    public WalletResponse getWallet(
            @AuthenticationPrincipal User user) {

        return walletService.getWallet(user);
    }
}