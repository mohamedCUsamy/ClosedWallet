package com.closedwallet.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;



public class TransferRequest {

    @NotNull
    private Long senderWalletId;

    private String receiverPhone;

    private String receiverEmail;

    @NotNull
    @Positive
    private BigDecimal amount;

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(Long senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}