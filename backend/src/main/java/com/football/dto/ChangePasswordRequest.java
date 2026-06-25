package com.football.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Parola curenta este obligatorie")
    public String currentPassword;

    @NotBlank(message = "Parola noua este obligatorie")
    @Size(min = 6, message = "Parola noua trebuie sa aiba minim 6 caractere")
    public String newPassword;
}
