package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Setter
@Getter
public class TopUpRequest {
    public BigDecimal amount;
    public boolean isValid;
}
