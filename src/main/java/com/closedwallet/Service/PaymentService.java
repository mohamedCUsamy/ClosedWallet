package com.closedwallet.Service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.User;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.dto.PaymentRequest;
import com.closedwallet.dto.PaymentResponse;
import com.closedwallet.enums.TransactionStatus;
import com.closedwallet.enums.TransactionType;
import com.closedwallet.enums.WalletStatus;

@Service
public class PaymentService {

    private final WalletRepository walletRepository;
    TransactionRepository transactionRepository;
    UserRepository userRepository;
    MerchantRepository merchantRepository;
    private final FailedTransactionRecorder failedTransactionRecorder;

    PaymentService(TransactionRepository transactionRepository, UserRepository userRepository, MerchantRepository merchantRepository, WalletRepository walletRepository, FailedTransactionRecorder failedTransactionRecorder) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
        this.failedTransactionRecorder = failedTransactionRecorder;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse processPayment(PaymentRequest paymentRequest, String email){
        PaymentResponse response = new PaymentResponse();
        User sender = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Merchant receiver = merchantRepository.findById(paymentRequest.getMerchantId()).orElseThrow(() -> new RuntimeException("Merchant not found"));
        BigDecimal amount = paymentRequest.getAmount();

        try {

            if((sender != null) && (receiver != null)){

                if (sender.getUserWallet().getStatus() != WalletStatus.ACTIVE) {
                    throw new IllegalStateException("Wallet is " + sender.getUserWallet().getStatus() + " and cannot be used for payments");
                }
                if (receiver.getWallet().getStatus() != WalletStatus.ACTIVE) {
                    throw new IllegalStateException("Merchant wallet is " + receiver.getWallet().getStatus() + " and cannot receive payments");
                }

                BigDecimal senderWalletBlance = sender.getUserWallet().getBalance();
                BigDecimal receiverWallet = receiver.getWallet().getBalance();

                if(senderWalletBlance.compareTo(amount) >= 0){

                
                    sender.getUserWallet().setBalance(senderWalletBlance.subtract(amount));
                    receiver.getWallet().setBalance(receiverWallet.add(amount));

                    Transaction transaction = new Transaction();
                    transaction.setSenderWallet(sender.getUserWallet());
                    transaction.setReceiverWallet(receiver.getWallet());
                    transaction.setAmount(amount);
                    transaction.setType(TransactionType.PAYMENT);
                    transaction.setReferenceId(UUID.randomUUID().toString());
                    transaction.setStatus(TransactionStatus.SUCCESS);
                    transactionRepository.save(transaction);

                    walletRepository.save(sender.getUserWallet());
                    walletRepository.save(receiver.getWallet());

                    response.setStatus(TransactionStatus.SUCCESS);
                    response.setReferenceId(transaction.getReferenceId());
                    response.setSenderBalance(sender.getUserWallet().getBalance());
                    response.setReceiverBalance(receiver.getWallet().getBalance());

                } else {
                    response.setStatus(TransactionStatus.FAILED);
                    throw new RuntimeException("Insufficient balance");
                }
            
            }
            else{
                response.setStatus(TransactionStatus.FAILED);
                throw new RuntimeException("Sender or receiver not found");
            }

        } catch (RuntimeException e) {
            // Recorded in its own transaction so the row survives this rollback.
            failedTransactionRecorder.record(
                    sender.getUserWallet() == null ? null : sender.getUserWallet().getId(),
                    receiver.getWallet() == null ? null : receiver.getWallet().getId(),
                    amount,
                    TransactionType.PAYMENT,
                    e.getMessage());
            throw e;
        }
        return response;
    }
    
}
