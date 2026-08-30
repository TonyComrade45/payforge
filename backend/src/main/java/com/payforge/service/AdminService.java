package com.payforge.service;

import com.payforge.dto.response.AdminStatsResponse;
import com.payforge.dto.response.AdminUserDetailsResponse;
import com.payforge.dto.response.AdminUserResponse;
import com.payforge.entity.User;
import com.payforge.exception.ResourceNotFoundException;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.payforge.dto.response.AdminUserDetailsResponse;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;

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
    public Page<AdminUserResponse> getUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return users.map(user ->
                new AdminUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }

    public AdminUserDetailsResponse getUserDetails(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found"));

        long transactionCount =
                transactionRepository.countByWalletUserId(userId);

        return new AdminUserDetailsResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.isActive(),
                transactionCount
        );
    }
}