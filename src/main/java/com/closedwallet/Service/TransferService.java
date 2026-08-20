package com.closedwallet.Service;

import com.closedwallet.Entity.Transaction;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.enums.TransactionStatus;
import com.closedwallet.enums.TransactionType;
import org.springframework.stereotype.Service;
import com.closedwallet.dto.TransferRequest;
import org.springframework.transaction.annotation.Transactional;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import java.math.BigDecimal;
import java.util.UUID;
import com.closedwallet.Exception.WalletNotFoundException;

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

    // @Transactional
    // public void transfer(TransferRequest request) {

    //     Wallet senderWallet = walletRepository.findById(request.getSenderWalletId())
    //             .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found"));

    //     User receiver;

    //     if (request.getReceiverEmail() != null && !request.getReceiverEmail().isBlank()) {

    //         receiver = userRepository.findByEmail(request.getReceiverEmail())
    //                 .orElseThrow(() ->new RuntimeException("Receiver not found"));

    //     } else if (request.getReceiverPhone() != null && !request.getReceiverPhone().isBlank()) {

    //         receiver = userRepository.findByPhoneNumber(request.getReceiverPhone())
    //                 .orElseThrow(() -> new RuntimeException("Receiver not found"));

    //     } else {

    //         throw new RuntimeException("Receiver email or phone is required");
    //     }
    //     Wallet receiverWallet = receiver.getWallet();
    //     if(receiverWallet==null){
    //         throw new RuntimeException("Receiver wallet not found");
    //     }
    //     if (senderWallet.getId().equals(receiverWallet.getId())) {
    //         throw new RuntimeException("Cannot transfer money to your own wallet");
    //     }
    //     if (senderWallet.getCurrency() != receiverWallet.getCurrency()) {
    //         throw new RuntimeException("Wallet currencies do not match");
    //     }
    //     if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
    //         throw new RuntimeException("Amount must be greater than zero");
    //     }
    //     if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
    //         throw new RuntimeException("Insufficient balance");
    //     }
    //     senderWallet.setBalance(
    //             senderWallet.getBalance().subtract(request.getAmount())
    //     );
    //     receiverWallet.setBalance(
    //             receiverWallet.getBalance().add(request.getAmount())
    //     );
    //     Transaction transaction= new Transaction();
    //     transaction.setSenderWallet(senderWallet);
    //     transaction.setReceiverWallet(receiverWallet);
    //     transaction.setAmount(request.getAmount());
    //     transaction.setType(TransactionType.TRANSFER);
    //     transaction.setStatus(TransactionStatus.SUCCESS);
    //     transaction.setReferenceId(UUID.randomUUID().toString());

    //     walletRepository.save(senderWallet);
    //     walletRepository.save(receiverWallet);
    //     transactionRepository.save(transaction);
    // }

}