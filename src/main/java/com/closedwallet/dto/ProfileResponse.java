package com.closedwallet.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProfileResponse {
    private String name;
    private String email;
    private String phoneNumber;

    public ProfileResponse(String email, String phoneNumber, String name) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public ProfileResponse() {
    }


}
