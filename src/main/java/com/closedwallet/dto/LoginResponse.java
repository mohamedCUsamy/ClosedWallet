package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String responseCode;
    private String responseMessage;
    private String responseDescription;
    private String token;

}
