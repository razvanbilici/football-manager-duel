package com.football.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data public class AddPlayerRequest {
    @NotNull public Long playerId;
    @NotNull public Integer slotNumber;
}
