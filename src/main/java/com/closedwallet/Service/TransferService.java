package com.closedwallet.Service;

import com.closedwallet.Entity.Transaction;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.dto.TransactionsResponse;
import com.closedwallet.dto.TransferResponse;
import com.closedwallet.dto.WeeklySpendingResponse;
import com.closedwallet.enums.TransactionStatus;
import com.closedwallet.enums.TransactionType;
import com.closedwallet.enums.WalletStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.closedwallet.dto.TransferRequest;
import org.springframework.transaction.annotation.Transactional;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class TransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final FailedTransactionRecorder failedTransactionRecorder;

    public TransferService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            FailedTransactionRecorder failedTransactionRecorder) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.failedTransactionRecorder = failedTransactionRecorder;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request, Authentication authentication) {
        User sender = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));
        Wallet senderWallet = sender.getWallet();
        User receiver;
        Long receiverWalletId = null;

        try {

            if (request.getReceiverEmail() != null && !request.getReceiverEmail().isBlank()) {
                receiver = userRepository.findByEmail(request.getReceiverEmail())
                        .orElseThrow(() -> new RuntimeException("Receiver not found"));

            } else {
                throw new RuntimeException("Receiver email is required");
            }

            Wallet receiverWallet = receiver.getWallet();
            if (receiverWallet == null) {
                throw new RuntimeException("Receiver wallet not found");
            }
            receiverWalletId = receiverWallet.getId();
            if (senderWallet.getId().equals(receiverWallet.getId())) {
                throw new RuntimeException("Cannot transfer money to your own wallet");
            }
            if (senderWallet.getCurrency() != receiverWallet.getCurrency()) {
                throw new RuntimeException("Wallet currencies do not match");
            }
            if (senderWallet.getStatus() != WalletStatus.ACTIVE) {
                throw new IllegalStateException("Sender wallet is " + senderWallet.getStatus() + " and cannot send money");
            }
            if (receiverWallet.getStatus() != WalletStatus.ACTIVE) {
                throw new IllegalStateException("Receiver wallet is " + receiverWallet.getStatus() + " and cannot receive money");
            }
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Amount must be greater than zero");
            }
            if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
            receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
            walletRepository.save(senderWallet);
            walletRepository.save(receiverWallet);

            Transaction transaction = new Transaction();
            transaction.setSenderWallet(senderWallet);
            transaction.setReceiverWallet(receiverWallet);
            transaction.setAmount(request.getAmount());
            transaction.setType(TransactionType.TRANSFER);
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setReferenceId(UUID.randomUUID().toString());
            Transaction saved = transactionRepository.save(transaction);

            return new TransferResponse("Transfer successful", saved.getSenderWallet().getId(), saved.getAmount());

        } catch (RuntimeException e) {
            // Recorded in its own transaction so the row survives this rollback.
            failedTransactionRecorder.record(
                    senderWallet == null ? null : senderWallet.getId(),
                    receiverWalletId,
                    request.getAmount(),
                    TransactionType.TRANSFER,
                    e.getMessage());
            throw e;
        }
    }

    public WeeklySpendingResponse getWeeklySpending(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Wallet userWallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<Transaction> weeklyTransactions = transactionRepository.findByWalletAndDateRange(
                userWallet,
                weekAgo.atStartOfDay()
        );

        List<BigDecimal> spending = new ArrayList<>();
        List<BigDecimal> income = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            spending.add(BigDecimal.ZERO);
            income.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : weeklyTransactions) {
            if (transaction.getStatus() != TransactionStatus.SUCCESS) {
                continue;
            }

            long daysDifference = ChronoUnit.DAYS.between(
                    weekAgo,
                    transaction.getCreatedAt().toLocalDate()
            );
            int dayIndex = (int) daysDifference;

            if (dayIndex >= 0 && dayIndex < 7) {
                if (transaction.getSenderWallet().getId().equals(userWallet.getId())) {
                    BigDecimal currentSpending = spending.get(dayIndex);
                    spending.set(dayIndex, currentSpending.add(transaction.getAmount()));
                } else if (transaction.getReceiverWallet().getId().equals(userWallet.getId())) {
                    BigDecimal currentIncome = income.get(dayIndex);
                    income.set(dayIndex, currentIncome.add(transaction.getAmount()));
                }
            }
        }

        return new WeeklySpendingResponse(spending, income);
    }

    public List<TransactionsResponse> getAllTransactions(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        Wallet userWallet =walletRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Wallet not found"));
        List<Transaction> t = transactionRepository.findByWalletAndDateRange(userWallet, LocalDateTime.now().minusDays(7));
        List<TransactionsResponse> response = new ArrayList<TransactionsResponse>();
        while (!t.isEmpty()){
            response.add(mapToDTO(t.removeFirst()));
        }
        return response;
    }
    private TransactionsResponse mapToDTO(Transaction transaction) {
        Wallet t = transaction.getReceiverWallet();
        if(t.getUser() == null){
            return new TransactionsResponse(
                    transaction.getId(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getStatus(),
                    transaction.getReferenceId(),
                    transaction.getCreatedAt(),
                    transaction.getSenderWallet().getUser().getName(),
                    transaction.getReceiverWallet().getMerchant().getName(),
                    transaction.getSenderWallet().getCurrency()
            );
        }
        return new TransactionsResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getReferenceId(),
                transaction.getCreatedAt(),
                transaction.getSenderWallet().getUser().getName(),
                transaction.getReceiverWallet().getUser().getName(),
                transaction.getSenderWallet().getCurrency()
                );
    }
}