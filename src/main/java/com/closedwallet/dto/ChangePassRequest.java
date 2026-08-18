package com.closedwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePassRequest {
    @NotNull
    @NotBlank
    @Size(min = 8, max = 30)
    private String password;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 30)
    private String confirmPassword;


}
