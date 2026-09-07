package com.payforge.repository;

import com.payforge.entity.Refund;
import com.payforge.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RefundRepository
        extends JpaRepository<Refund, Long> {

    Optional<Refund> findByReferenceId(
            String referenceId
    );

    List<Refund> findByPaymentReferenceId(
            String paymentReferenceId
    );

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Refund r
            WHERE r.paymentReferenceId = :paymentReferenceId
            AND r.status = :status
            """)
    BigDecimal getTotalRefundedAmount(
            @Param("paymentReferenceId")
            String paymentReferenceId,
            @Param("status")
            TransactionStatus status
    );


}