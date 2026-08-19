package com.payforge.repository;

import com.payforge.entity.Transaction;
import com.payforge.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction ,Long> {
    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
}
