package com.closedwallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CreateAdminRequest {
    @NotBlank
    @Email(message ="please enter a valid email")
    private String email;
    @NotBlank
    @Size(min = 8, max = 50)
    private String password;
}
