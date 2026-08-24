package com.closedwallet.dto;

import com.closedwallet.enums.Role;
import lombok.Getter;
import lombok.Setter;
import com.closedwallet.enums.Role;

@Setter
@Getter
public class LoginResponse {
    private String responseCode;
    private String responseMessage;
    private String responseDescription;
    private String token;
    private Role role;

}
