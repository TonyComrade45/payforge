package com.payforge.repository;

import com.payforge.entity.Transaction;
import com.payforge.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction ,Long> {
    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);

    List<Transaction> findByWalletUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Transaction> findByIdAndWalletUserId(Long id, Long userId);
}
