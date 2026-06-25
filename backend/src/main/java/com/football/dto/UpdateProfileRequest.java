package com.football.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    public String name;

    @Email(message = "Email invalid")
    public String email;
}
