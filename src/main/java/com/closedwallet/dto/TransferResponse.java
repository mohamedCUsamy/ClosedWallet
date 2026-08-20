package com.closedwallet.dto;
import java.math.BigDecimal;
public class TransferResponse {

    private String message;
    private Long senderWalletId;
    private BigDecimal amount;

    public TransferResponse(String message, Long senderWalletId, BigDecimal amount) {
        this.message = message;
        this.senderWalletId = senderWalletId;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
