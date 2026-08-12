package com.closedwallet.Entity;

import java.util.Date;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;

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
