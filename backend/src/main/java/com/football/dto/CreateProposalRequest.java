package com.football.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProposalRequest {
    @NotNull(message = "Listarea este obligatorie")
    public Long playerId;

    @NotNull
    public Long fromUserTeamId;

    @NotNull(message = "Suma oferita este obligatorie")
    @Min(value = 1, message = "Suma trebuie sa fie pozitiva")
    public BigDecimal offeredPrice;
}
