package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
    private String responseCode;
    private String responseMessage;
    private String responseDescription;
}
