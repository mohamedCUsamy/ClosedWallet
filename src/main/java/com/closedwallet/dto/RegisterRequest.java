package com.closedwallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    @NotBlank
    @Email(message = "Enter valid email")
    private String email;
    @NotBlank
    @Pattern(regexp = "^01[0125][0-9]{8}$"
            ,message = "Enter valid number")
    private String phoneNumber;
    @NotBlank
    @Size(min = 8, max = 50, message = "Password must be at least 8 characters")
    private String password;
    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, max = 50, message = "Password must be at least 8 characters")
    private String confirmPassword;
}
