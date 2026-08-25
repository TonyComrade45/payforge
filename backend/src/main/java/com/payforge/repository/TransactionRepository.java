package com.payforge.repository;

import com.payforge.entity.Transaction;
import com.payforge.entity.TransactionType;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);

    List<Transaction> findByWalletUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Transaction> findByIdAndWalletUserId(
            Long id,
            Long userId
    );

    Page<Transaction> findByWalletUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Page<Transaction> findByWalletUserIdAndTypeOrderByCreatedAtDesc(
            Long userId,
            TransactionType type,
            Pageable pageable
    );
}