package com.payforge.service;

import com.payforge.dto.response.TransactionResponse;
import com.payforge.dto.response.WalletResponse;
import com.payforge.entity.Transaction;
import com.payforge.entity.TransactionType;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    // =========================
    // GET WALLET
    // =========================

    public WalletResponse getWallet(User user) {

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.isActive()
        );
    }

    // =========================
    // DEPOSIT
    // =========================

    @Transactional
    public WalletResponse deposit(Long userId, BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        if (!wallet.isActive()) {
            throw new RuntimeException("Wallet is inactive");
        }

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);

        transactionRepository.save(transaction);

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.isActive()
        );
    }

    // =========================
    // WITHDRAW
    // =========================

    @Transactional
    public WalletResponse withdraw(Long userId, BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        if (!wallet.isActive()) {
            throw new RuntimeException("Wallet is inactive");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(amount)
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(amount);

        transactionRepository.save(transaction);

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.isActive()
        );
    }

    // =========================
    // TRANSACTION HISTORY
    // WITH PAGINATION + FILTER
    // =========================

    public Page<TransactionResponse> getTransactions(
            Long userId,
            TransactionType type,
            Pageable pageable) {

        Page<Transaction> transactions;

        if (type == null) {

            transactions =
                    transactionRepository
                            .findByWalletUserIdOrderByCreatedAtDesc(
                                    userId,
                                    pageable
                            );

        } else {

            transactions =
                    transactionRepository
                            .findByWalletUserIdAndTypeOrderByCreatedAtDesc(
                                    userId,
                                    type,
                                    pageable
                            );
        }

        return transactions.map(transaction ->
                new TransactionResponse(
                        transaction.getId(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getCreatedAt()
                )
        );
    }

    // =========================
    // SINGLE TRANSACTION
    // =========================

    public TransactionResponse getTransaction(
            Long transactionId,
            Long userId) {

        Transaction transaction =
                transactionRepository
                        .findByIdAndWalletUserId(
                                transactionId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"));

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCreatedAt()
        );
    }
}