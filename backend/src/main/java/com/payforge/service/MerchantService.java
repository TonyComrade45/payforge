package com.payforge.service;

import com.payforge.dto.response.AdminTransactionResponse;
import com.payforge.entity.Transaction;
import com.payforge.entity.User;
import com.payforge.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {

    private final TransactionRepository transactionRepository;

    public MerchantService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<AdminTransactionResponse> getTransactions(
            User merchant,
            Pageable pageable) {

        Page<Transaction> transactions =
                transactionRepository
                        .findByWalletUserIdOrderByCreatedAtDesc(
                                merchant.getId(),
                                pageable
                        );

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