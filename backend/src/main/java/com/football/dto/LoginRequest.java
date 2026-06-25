package com.football.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Emailul este obligatoriu")
    @Email(message = "Email invalid")
    public String email;

    @NotBlank(message = "Parola este obligatorie")
    public String password;

    public boolean rememberMe = false;
}
