package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateResponse {
    private String responseCode;
    private String responseMessage;
    private String responseDescription;
}
