package com.football.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Numele este obligatoriu")
    public String name;

    @NotBlank(message = "Emailul este obligatoriu")
    @Email(message = "Email invalid")
    public String email;

    @NotBlank(message = "Parola este obligatorie")
    @Size(min = 6, message = "Parola trebuie sa aiba minim 6 caractere")
    public String password;
}
