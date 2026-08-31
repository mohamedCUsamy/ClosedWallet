package com.closedwallet.dto;

import com.closedwallet.enums.TransactionStatus;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentResponse {
    TransactionStatus Status;
    BigDecimal senderBalance;
    BigDecimal receiverBalance;
    String referenceId;

    public PaymentResponse() {
        senderBalance = BigDecimal.ZERO;
        receiverBalance = BigDecimal.ZERO;
    }

    public BigDecimal getSenderBalance() {
        return senderBalance;
    }

    public void setSenderBalance(BigDecimal senderBalance) {
        this.senderBalance = senderBalance;
    }

    public BigDecimal getReceiverBalance() {
        return receiverBalance;
    }

    public void setReceiverBalance(BigDecimal receiverBalance) {
        this.receiverBalance = receiverBalance;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public TransactionStatus getStatus() {
        return Status;
    }

    public void setStatus(TransactionStatus status) {
        Status = status;
    }
}
