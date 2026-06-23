package com.football.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data public class CreateFormationRequest {
    @NotBlank public String name;
    public String description;
}
