package com.closedwallet.dto;

import java.math.BigDecimal;

import com.closedwallet.Entity.Merchant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentRequest {
    //private Long merchantId;
    private BigDecimal amount;
    //private Merchant merchant;
    private Long merchantId;
    // private String currency;
    // private String description;
    
}
