package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateAdminResponse {
    private String responseCode;
    private String responseMessage;

    public CreateAdminResponse(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}
