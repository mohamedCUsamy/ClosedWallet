package com.closedwallet.dto;

import com.closedwallet.enums.MerchantCategory;
import com.closedwallet.enums.WalletStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
public class AdminMerchantResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private MerchantCategory category;
    private Long walletId;
    private BigDecimal balance;
    private WalletStatus walletStatus;

    public AdminMerchantResponse() {
    }
}
