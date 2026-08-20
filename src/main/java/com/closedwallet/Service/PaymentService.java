package com.closedwallet.Service;

import java.math.BigDecimal;

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

@Service
public class PaymentService {

    private final WalletRepository walletRepository;
    TransactionRepository transactionRepository;
    UserRepository userRepository;
    MerchantRepository merchantRepository;

    PaymentService(TransactionRepository transactionRepository, UserRepository userRepository, MerchantRepository merchantRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse processPayment(PaymentRequest paymentRequest, String email){
        PaymentResponse response = new PaymentResponse();
        User sender = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Merchant receiver = merchantRepository.findById(paymentRequest.getMerchantId()).orElseThrow(() -> new RuntimeException("Merchant not found"));
        BigDecimal amount = paymentRequest.getAmount();
        


        if((sender != null) && (receiver != null)){

            BigDecimal senderWalletBlance = sender.getUserWallet().getBalance();
            BigDecimal receiverWallet = receiver.getWallet().getBalance();

            if(senderWalletBlance.compareTo(amount) >= 0){

                
                sender.getUserWallet().setBalance(senderWalletBlance.subtract(amount));
                receiver.getWallet().setBalance(receiverWallet.add(amount));

                Transaction transaction = new Transaction();
                transaction.setSenderWallet(sender.getUserWallet());
                transaction.setReceiverWallet(receiver.getWallet());
                transaction.setAmount(amount);
                transaction.setStatus(TransactionStatus.SUCCESS);
                transactionRepository.save(transaction);

                walletRepository.save(sender.getUserWallet());
                walletRepository.save(receiver.getWallet());

                response.setStatus(TransactionStatus.SUCCESS);
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
        return response;
    }
    
}
