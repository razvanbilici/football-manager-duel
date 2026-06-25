package com.football.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserTeamRequest {
    @NotBlank(message = "Numele echipei este obligatoriu")
    public String name;

    public Long formationId;
    public Long tacticId;
}
