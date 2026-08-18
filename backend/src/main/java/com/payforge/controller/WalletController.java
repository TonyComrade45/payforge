package com.payforge.controller;

import com.payforge.dto.request.DepositRequest;
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
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        wallet.setBalance(
                wallet.getBalance().add(request.getAmount())
        );

        Wallet savedWallet = walletRepository.save(wallet);

        WalletResponse response = new WalletResponse(
                savedWallet.getId(),
                savedWallet.getBalance(),
                savedWallet.getCurrency(),
                savedWallet.isActive()
        );

        return ResponseEntity.ok(response);
    }
}