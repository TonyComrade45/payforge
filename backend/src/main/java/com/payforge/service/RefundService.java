package com.payforge.service;

import com.payforge.dto.request.RefundRequest;
import com.payforge.dto.response.RefundResponse;
import com.payforge.entity.*;
import com.payforge.exception.BadRequestException;
import com.payforge.exception.DuplicateRequestException;
import com.payforge.exception.ResourceNotFoundException;
import com.payforge.repository.IdempotencyRepository;
import com.payforge.repository.RefundRepository;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RefundService {

    private final TransactionRepository transactionRepository;
    private final RefundRepository refundRepository;
    private final WalletRepository walletRepository;
    private final IdempotencyRepository idempotencyRepository;

    public RefundService(
            TransactionRepository transactionRepository,
            RefundRepository refundRepository,
            WalletRepository walletRepository,
            IdempotencyRepository idempotencyRepository) {

        this.transactionRepository = transactionRepository;
        this.refundRepository = refundRepository;
        this.walletRepository = walletRepository;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Transactional
    public RefundResponse refund(
            User customer,
            RefundRequest request) {

        // 1. Idempotency
        if (idempotencyRepository.existsByIdempotencyKey(
                request.getIdempotencyKey())) {

            throw new DuplicateRequestException(
                    "Duplicate refund request");
        }

        // 2. Find all transactions belonging to this payment
        List<Transaction> paymentTransactions =
                transactionRepository
                        .findByPaymentReferenceId(
                                request.getPaymentReferenceId()
                        );

        if (paymentTransactions.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Payment not found");
        }

        // 3. Find customer and merchant transactions
        Transaction customerTransaction = null;
        Transaction merchantTransaction = null;

        for (Transaction transaction :
                paymentTransactions) {

            User transactionUser =
                    transaction.getWallet().getUser();

            if (transactionUser.getId()
                    .equals(customer.getId())) {

                customerTransaction = transaction;

            } else if (
                    transactionUser.getRole()
                            == Role.MERCHANT) {

                merchantTransaction = transaction;
            }
        }

        // 4. Validate payment structure
        if (customerTransaction == null) {
            throw new BadRequestException(
                    "Payment does not belong to customer");
        }

        if (merchantTransaction == null) {
            throw new BadRequestException(
                    "Payment does not belong to a merchant");
        }

        // 5. Payment must be successful
        if (customerTransaction.getStatus()
                != TransactionStatus.SUCCESS) {

            throw new BadRequestException(
                    "Payment was not successful");
        }

        if (merchantTransaction.getStatus()
                != TransactionStatus.SUCCESS) {

            throw new BadRequestException(
                    "Payment was not successful");
        }

        // 6. Refund amount cannot exceed original payment
        BigDecimal originalAmount =
                merchantTransaction.getAmount();

        BigDecimal totalRefunded =
                refundRepository.getTotalRefundedAmount(
                        request.getPaymentReferenceId(),
                        TransactionStatus.SUCCESS
                );

        if (totalRefunded == null) {
            totalRefunded = BigDecimal.ZERO;
        }

        BigDecimal remainingAmount =
                originalAmount.subtract(totalRefunded);

        if (request.getAmount()
                .compareTo(remainingAmount) > 0) {

            throw new BadRequestException(
                    "Refund amount exceeds refundable amount");
        }

        // 7. Get wallets
        Wallet customerWallet =
                customerTransaction.getWallet();

        Wallet merchantWallet =
                merchantTransaction.getWallet();

        // 8. Lock wallets in deterministic order
        Wallet lockedCustomerWallet;
        Wallet lockedMerchantWallet;

        if (customerWallet.getId()
                < merchantWallet.getId()) {

            lockedCustomerWallet =
                    walletRepository
                            .findByIdForUpdate(
                                    customerWallet.getId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Customer wallet not found"));

            lockedMerchantWallet =
                    walletRepository
                            .findByIdForUpdate(
                                    merchantWallet.getId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Merchant wallet not found"));

        } else {

            lockedMerchantWallet =
                    walletRepository
                            .findByIdForUpdate(
                                    merchantWallet.getId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Merchant wallet not found"));

            lockedCustomerWallet =
                    walletRepository
                            .findByIdForUpdate(
                                    customerWallet.getId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Customer wallet not found"));
        }

        // 9. Check wallets
        if (!lockedMerchantWallet.isActive()) {
            throw new BadRequestException(
                    "Merchant wallet is inactive");
        }

        if (!lockedCustomerWallet.isActive()) {
            throw new BadRequestException(
                    "Customer wallet is inactive");
        }

        // 10. Merchant must have enough money
        if (lockedMerchantWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Merchant has insufficient balance for refund");
        }

        // 11. Move money back
        lockedMerchantWallet.setBalance(
                lockedMerchantWallet.getBalance()
                        .subtract(request.getAmount()));

        lockedCustomerWallet.setBalance(
                lockedCustomerWallet.getBalance()
                        .add(request.getAmount()));

        walletRepository.save(lockedMerchantWallet);
        walletRepository.save(lockedCustomerWallet);

        // 12. Generate refund reference
        String refundReferenceId =
                "REF-" + UUID.randomUUID();

        // 13. Save refund record
        Refund refund = new Refund();

        refund.setReferenceId(
                refundReferenceId);

        refund.setPaymentReferenceId(
                request.getPaymentReferenceId());

        refund.setCustomer(
                customer);

        refund.setMerchant(
                merchantTransaction
                        .getWallet()
                        .getUser());

        refund.setAmount(
                request.getAmount());

        refund.setStatus(
                TransactionStatus.SUCCESS);

        refundRepository.save(refund);

        // 14. Customer refund transaction
        Transaction customerRefund =
                new Transaction();

        customerRefund.setWallet(
                lockedCustomerWallet);

        customerRefund.setType(
                TransactionType.REFUND);

        customerRefund.setStatus(
                TransactionStatus.SUCCESS);

        customerRefund.setReferenceId(
                "TXN-" + UUID.randomUUID());

        customerRefund.setPaymentReferenceId(
                request.getPaymentReferenceId());

        customerRefund.setAmount(
                request.getAmount());

        transactionRepository.save(
                customerRefund);

        // 15. Merchant refund transaction
        Transaction merchantRefund =
                new Transaction();

        merchantRefund.setWallet(
                lockedMerchantWallet);

        merchantRefund.setType(
                TransactionType.REFUND);

        merchantRefund.setStatus(
                TransactionStatus.SUCCESS);

        merchantRefund.setReferenceId(
                "TXN-" + UUID.randomUUID());

        merchantRefund.setPaymentReferenceId(
                request.getPaymentReferenceId());

        merchantRefund.setAmount(
                request.getAmount());

        transactionRepository.save(
                merchantRefund);

        // 16. Save idempotency record
        IdempotencyRecord idempotencyRecord =
                new IdempotencyRecord();

        idempotencyRecord.setIdempotencyKey(
                request.getIdempotencyKey());

        idempotencyRepository.save(
                idempotencyRecord);

        // 17. Response
        return new RefundResponse(
                "Refund successful",
                refundReferenceId,
                request.getAmount()
        );
    }
}