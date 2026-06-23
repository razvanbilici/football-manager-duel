package com.football.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data public class CreatePlayerTeamRequest {
    @NotBlank public String name;
    public Integer ucl = 0;
    public Integer league = 0;
    public Integer cup = 0;
}
