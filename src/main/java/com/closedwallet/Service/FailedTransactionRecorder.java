package com.closedwallet.Service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.enums.TransactionStatus;
import com.closedwallet.enums.TransactionType;

/**
 * Persists a FAILED transaction row for a payment/transfer that was rejected.
 *
 * This lives in its own bean on purpose. The row is written with
 * REQUIRES_NEW so it commits in a separate transaction and survives the
 * rollback of the failing operation. Calling a REQUIRES_NEW method on the
 * same bean would go through "this" and bypass the Spring proxy, so the
 * new transaction would never start.
 */
@Service
public class FailedTransactionRecorder {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public FailedTransactionRecorder(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository) {

        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Long senderWalletId,
                       Long receiverWalletId,
                       BigDecimal amount,
                       TransactionType type,
                       String failureReason) {

        // Reload inside this transaction; the caller's entities belong to a
        // persistence context that is about to be rolled back.
        Wallet senderWallet = senderWalletId == null
                ? null
                : walletRepository.findById(senderWalletId).orElse(null);

        Wallet receiverWallet = receiverWalletId == null
                ? null
                : walletRepository.findById(receiverWalletId).orElse(null);

        Transaction transaction = new Transaction();
        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setReferenceId(UUID.randomUUID().toString());
        transaction.setFailureReason(failureReason);

        transactionRepository.save(transaction);
    }
}
