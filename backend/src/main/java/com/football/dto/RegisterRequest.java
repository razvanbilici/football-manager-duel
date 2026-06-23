package com.football.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data public class RegisterRequest {
    @NotBlank public String name;
    @NotBlank public String email;
    @NotBlank public String password;
}
