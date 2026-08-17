package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ChangePassResponse {
    private String responseCode;
    private String responseMessage;

    public ChangePassResponse(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}
