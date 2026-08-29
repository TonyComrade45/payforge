package com.payforge.service;

import com.payforge.dto.response.AdminStatsResponse;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public AdminStatsResponse getStats() {

        long totalUsers = userRepository.count();
        long totalWallets = walletRepository.count();
        long totalTransactions = transactionRepository.count();

        return new AdminStatsResponse(
                totalUsers,
                totalWallets,
                totalTransactions
        );
    }
}