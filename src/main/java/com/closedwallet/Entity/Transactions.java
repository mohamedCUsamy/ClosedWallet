package com.closedwallet.Entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Transactions")
public class Transactions {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Id generation
    private BigDecimal id;

    private enum type {
        TOPUP,
        PAYMENT,
        TRANSFER,
        REFUND
    }

    @Nullable
    private BigDecimal sender_wallet_id;
    private BigDecimal receiver_wallet_id;
    private BigDecimal amount;
    private enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }
    private BigDecimal reference_id;
    private Date created_at;

}
