package com.payforge.service;

import com.payforge.dto.response.PaymentDetailsResponse;
import com.payforge.entity.*;
import com.payforge.exception.BadRequestException;
import com.payforge.exception.ResourceNotFoundException;
import com.payforge.repository.RefundRepository;
import com.payforge.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentDetailsService {

    private final TransactionRepository transactionRepository;
    private final RefundRepository refundRepository;

    public PaymentDetailsService(
            TransactionRepository transactionRepository,
            RefundRepository refundRepository) {

        this.transactionRepository = transactionRepository;
        this.refundRepository = refundRepository;
    }

    public PaymentDetailsResponse getPaymentDetails(
            User customer,
            String paymentReferenceId) {

        List<Transaction> transactions =
                transactionRepository
                        .findByPaymentReferenceId(
                                paymentReferenceId
                        );

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Payment not found"
            );
        }

        Transaction customerTransaction = null;
        Transaction merchantTransaction = null;

        for (Transaction transaction : transactions) {

            User user =
                    transaction.getWallet().getUser();

            if (user.getId().equals(customer.getId())) {
                customerTransaction = transaction;
            }

            if (user.getRole() == Role.MERCHANT) {
                merchantTransaction = transaction;
            }
        }

        if (customerTransaction == null) {
            throw new BadRequestException(
                    "Payment does not belong to customer"
            );
        }

        if (merchantTransaction == null) {
            throw new BadRequestException(
                    "Merchant transaction not found"
            );
        }

        BigDecimal originalAmount =
                merchantTransaction.getAmount();

        BigDecimal refundedAmount =
                refundRepository.getTotalRefundedAmount(
                        paymentReferenceId,
                        TransactionStatus.SUCCESS
                );

        if (refundedAmount == null) {
            refundedAmount = BigDecimal.ZERO;
        }

        BigDecimal remainingRefundableAmount =
                originalAmount.subtract(refundedAmount);

        return new PaymentDetailsResponse(
                paymentReferenceId,
                customerTransaction
                        .getWallet()
                        .getUser()
                        .getEmail(),
                merchantTransaction
                        .getWallet()
                        .getUser()
                        .getEmail(),
                originalAmount,
                refundedAmount,
                remainingRefundableAmount,
                customerTransaction
                        .getStatus()
                        .name(),
                customerTransaction.getCreatedAt()
        );
    }
}