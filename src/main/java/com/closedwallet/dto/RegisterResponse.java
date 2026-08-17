package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterResponse {
    private String responseCode;
    private String responseMessage;
    private String responseDescription;
}
