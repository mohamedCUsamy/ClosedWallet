package com.closedwallet.dto;

import com.closedwallet.Entity.Transaction;
import com.closedwallet.enums.Currency;
import com.closedwallet.enums.TransactionStatus;
import com.closedwallet.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class TransactionsResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String referenceId;
    private LocalDateTime createdAt;
    private Currency currency;
    private String senderName;
    private String receiverName;
    public TransactionsResponse(
            Long id,
            BigDecimal amount,
            TransactionType type,
            TransactionStatus status,
            String referenceId,
            LocalDateTime createdAt,
            String senderName,
            String receiverName,
            Currency currency) {

        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.currency = currency;
    }
}
