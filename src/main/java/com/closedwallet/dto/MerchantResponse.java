package com.closedwallet.dto;

import com.closedwallet.enums.MerchantCategory;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MerchantResponse {
    private long id;
    private String name;
    private MerchantCategory category;
    private String logoPath;
}
