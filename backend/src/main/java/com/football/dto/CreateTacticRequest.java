package com.football.dto;
import com.football.entity.Tactic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data public class CreateTacticRequest {
    @NotBlank public String details;
    @NotNull public Tactic.Style style;
}
