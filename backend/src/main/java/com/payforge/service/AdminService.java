package com.payforge.service;

import com.payforge.dto.request.CreateUserRequest;
import com.payforge.dto.response.*;
import com.payforge.entity.Transaction;
import com.payforge.entity.TransactionType;
import com.payforge.entity.User;
import com.payforge.exception.BadRequestException;
import com.payforge.exception.ResourceNotFoundException;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.payforge.dto.response.AdminUserDetailsResponse;
import com.payforge.entity.Wallet;

import java.math.BigDecimal;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    public AdminService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
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

    public Page<AdminTransactionResponse> getTransactions(
            Pageable pageable) {

        Page<Transaction> transactions =
                transactionRepository
                        .findAllByOrderByCreatedAtDesc(pageable);

        return transactions.map(transaction ->
                new AdminTransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getUser().getId(),
                        transaction.getWallet().getUser().getEmail(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getStatus(),
                        transaction.getReferenceId(),
                        transaction.getCreatedAt()
                )
        );
    }
    @Transactional
    public AdminUserResponse createUser(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        // Create wallet for the new user
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setActive(true);

        walletRepository.save(wallet);

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public Page<AdminTransactionResponse> getTransactions(
            TransactionType type,
            Pageable pageable) {

        Page<Transaction> transactions;

        if (type == null) {

            transactions =
                    transactionRepository
                            .findAllByOrderByCreatedAtDesc(pageable);

        } else {

            transactions =
                    transactionRepository
                            .findByTypeOrderByCreatedAtDesc(
                                    type,
                                    pageable
                            );
        }

        return transactions.map(transaction ->
                new AdminTransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getUser().getId(),
                        transaction.getWallet().getUser().getEmail(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getStatus(),
                        transaction.getReferenceId(),
                        transaction.getCreatedAt()
                )
        );
    }

}