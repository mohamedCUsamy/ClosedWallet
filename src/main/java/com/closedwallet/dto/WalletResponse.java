package com.closedwallet.dto;

import com.closedwallet.Entity.Wallet;
import com.closedwallet.enums.Currency;
import java.math.BigDecimal;
import com.closedwallet.enums.WalletStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WalletResponse {
    //(wallet.getId(), wallet.getBalance(), wallet.getCurrency(), wallet.getStatus())
    private Long id;
    private BigDecimal balance;
    private Currency currency;
    private WalletStatus status;

    public WalletResponse(Long id, BigDecimal balance, Currency currency, WalletStatus status) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
    }
}
