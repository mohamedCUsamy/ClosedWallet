package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {
    private String email;
    private String phoneNumber;
    private String name;
    private String password;
}
