package com.payforge.controller;

import com.payforge.dto.request.DepositRequest;
import com.payforge.dto.request.WithdrawRequest;
import com.payforge.dto.response.WalletResponse;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;
import com.payforge.repository.WalletRepository;
import com.payforge.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletRepository walletRepository;

    public WalletController(
            WalletService walletService,
            WalletRepository walletRepository) {

        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    @GetMapping
    public WalletResponse getWallet(
            @AuthenticationPrincipal User user) {

        return walletService.getWallet(user);
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @RequestBody DepositRequest request,
            @AuthenticationPrincipal User user) {

        WalletResponse response =
                walletService.deposit(
                        user.getId(),
                        request.getAmount()
                );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal User user) {

        WalletResponse response =
                walletService.withdraw(
                        user.getId(),
                        request.getAmount()
                );

        return ResponseEntity.ok(response);
    }
}