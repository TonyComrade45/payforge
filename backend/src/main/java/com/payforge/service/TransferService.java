package com.payforge.service;

import com.payforge.dto.request.TransferRequest;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public TransferService(
            UserRepository userRepository,
            WalletRepository walletRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void transfer(User sender, TransferRequest request) {

        // 1. Validate amount
        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        // 2. Find receiver
        User receiver = userRepository
                .findByEmail(request.getReceiverEmail())
                .orElseThrow(() ->
                        new RuntimeException("Receiver not found"));

        // 3. Sender cannot transfer to himself
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException(
                    "Cannot transfer money to yourself");
        }

        // 4. Find sender wallet
        Wallet senderWallet = walletRepository
                .findByUser(sender)
                .orElseThrow(() ->
                        new RuntimeException("Sender wallet not found"));

        // 5. Find receiver wallet
        Wallet receiverWallet = walletRepository
                .findByUser(receiver)
                .orElseThrow(() ->
                        new RuntimeException("Receiver wallet not found"));

        // 6. Check wallets are active
        if (!senderWallet.isActive()) {
            throw new RuntimeException(
                    "Sender wallet is inactive");
        }

        if (!receiverWallet.isActive()) {
            throw new RuntimeException(
                    "Receiver wallet is inactive");
        }

        // 7. Check sufficient balance
        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new RuntimeException(
                    "Insufficient balance");
        }

        // 8. Deduct from sender
        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );

        // 9. Add to receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        // 10. Save both wallets
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
    }
}