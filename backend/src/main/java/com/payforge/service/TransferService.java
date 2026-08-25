package com.payforge.service;

import com.payforge.dto.request.TransferRequest;
import com.payforge.entity.Transaction;
import com.payforge.entity.TransactionType;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;
import com.payforge.repository.TransactionRepository;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transfer(User sender, TransferRequest request) {

        // 1. Validate amount
        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        // 2. Find receiver
        User receiver = userRepository
                .findByEmail(request.getReceiverEmail())
                .orElseThrow(() ->
                        new RuntimeException("Receiver not found"));

        // 3. Sender cannot transfer to himself
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException(
                    "Cannot transfer money to yourself");
        }

        // 4. Find wallets WITHOUT locking
        Wallet senderWallet = walletRepository
                .findByUser(sender)
                .orElseThrow(() ->
                        new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository
                .findByUser(receiver)
                .orElseThrow(() ->
                        new RuntimeException("Receiver wallet not found"));

        // 5. Determine locking order using wallet ID
        Long firstWalletId;
        Long secondWalletId;

        if (senderWallet.getId() < receiverWallet.getId()) {
            firstWalletId = senderWallet.getId();
            secondWalletId = receiverWallet.getId();
        } else {
            firstWalletId = receiverWallet.getId();
            secondWalletId = senderWallet.getId();
        }

        // 6. Lock first wallet
        Wallet firstWallet = walletRepository
                .findByIdForUpdate(firstWalletId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        // 7. Lock second wallet
        Wallet secondWallet = walletRepository
                .findByIdForUpdate(secondWalletId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        // 8. Use the LOCKED wallet objects
        if (senderWallet.getId().equals(firstWallet.getId())) {
            senderWallet = firstWallet;
            receiverWallet = secondWallet;
        } else {
            senderWallet = secondWallet;
            receiverWallet = firstWallet;
        }

        // 9. Check wallets are active
        if (!senderWallet.isActive()) {
            throw new RuntimeException(
                    "Sender wallet is inactive");
        }

        if (!receiverWallet.isActive()) {
            throw new RuntimeException(
                    "Receiver wallet is inactive");
        }

        // 10. Check balance
        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new RuntimeException(
                    "Insufficient balance");
        }

        // 11. Deduct from sender
        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );

        // 12. Add to receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        // 13. Save wallets
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 14. Create sender transaction
        Transaction senderTransaction = new Transaction();

        senderTransaction.setWallet(senderWallet);
        senderTransaction.setType(TransactionType.TRANSFER);
        senderTransaction.setAmount(request.getAmount());

        // 15. Create receiver transaction
        Transaction receiverTransaction = new Transaction();

        receiverTransaction.setWallet(receiverWallet);
        receiverTransaction.setType(TransactionType.TRANSFER);
        receiverTransaction.setAmount(request.getAmount());

        // 16. Save transactions
        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
    }
}