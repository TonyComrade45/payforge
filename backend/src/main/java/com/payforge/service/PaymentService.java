package com.payforge.service;

import com.payforge.dto.request.MerchantPaymentRequest;
import com.payforge.dto.response.PaymentResponse;
import com.payforge.entity.*;
import com.payforge.exception.BadRequestException;
import com.payforge.exception.DuplicateRequestException;
import com.payforge.exception.ResourceNotFoundException;
import com.payforge.repository.IdempotencyRepository;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;

    public PaymentService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            IdempotencyRepository idempotencyRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Transactional
    public PaymentResponse payMerchant(
            User customer,
            MerchantPaymentRequest request) {

        // 1. Check idempotency
        if (idempotencyRepository.existsByIdempotencyKey(
                request.getIdempotencyKey())) {

            throw new DuplicateRequestException(
                    "Duplicate payment request");
        }

        // 2. Find merchant
        User merchant = userRepository
                .findByEmail(request.getMerchantEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found"));

        // 3. Make sure receiver is actually a merchant
        if (merchant.getRole() != Role.MERCHANT) {
            throw new BadRequestException(
                    "Specified user is not a merchant");
        }

        // 4. Prevent self payment
        if (customer.getId().equals(merchant.getId())) {
            throw new BadRequestException(
                    "Cannot pay yourself");
        }

        // 5. Get wallets
        Wallet customerWallet = walletRepository
                .findByUser(customer)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer wallet not found"));

        Wallet merchantWallet = walletRepository
                .findByUser(merchant)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant wallet not found"));

        // 6. Lock wallets in deterministic order
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

        // 7. Check wallet status
        if (!lockedCustomerWallet.isActive()) {
            throw new BadRequestException(
                    "Customer wallet is inactive");
        }

        if (!lockedMerchantWallet.isActive()) {
            throw new BadRequestException(
                    "Merchant wallet is inactive");
        }

        // 8. Check balance
        if (lockedCustomerWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Insufficient balance");
        }

        // 9. Transfer money
        lockedCustomerWallet.setBalance(
                lockedCustomerWallet.getBalance()
                        .subtract(request.getAmount()));

        lockedMerchantWallet.setBalance(
                lockedMerchantWallet.getBalance()
                        .add(request.getAmount()));

        walletRepository.save(lockedCustomerWallet);
        walletRepository.save(lockedMerchantWallet);

        // 10. Generate payment reference
        String referenceId =
                "PAY-" + UUID.randomUUID();

        // 11. Customer debit transaction
        Transaction customerTransaction =
                new Transaction();

        customerTransaction.setWallet(
                lockedCustomerWallet);

        customerTransaction.setType(
                TransactionType.TRANSFER);

        customerTransaction.setStatus(
                TransactionStatus.SUCCESS);

        customerTransaction.setReferenceId(
                referenceId);

        customerTransaction.setAmount(
                request.getAmount());

        transactionRepository.save(customerTransaction);

        // 12. Merchant credit transaction
        Transaction merchantTransaction =
                new Transaction();

        merchantTransaction.setWallet(
                lockedMerchantWallet);

        merchantTransaction.setType(
                TransactionType.TRANSFER);

        merchantTransaction.setStatus(
                TransactionStatus.SUCCESS);

        merchantTransaction.setReferenceId(
                "PAY-" + UUID.randomUUID());

        merchantTransaction.setAmount(
                request.getAmount());

        transactionRepository.save(merchantTransaction);

        // 13. Save idempotency record
        IdempotencyRecord idempotencyRecord =
                new IdempotencyRecord();

        idempotencyRecord.setIdempotencyKey(
                request.getIdempotencyKey());

        idempotencyRepository.save(idempotencyRecord);

        return new PaymentResponse(
                "Payment successful",
                referenceId,
                request.getAmount()
        );
    }
}