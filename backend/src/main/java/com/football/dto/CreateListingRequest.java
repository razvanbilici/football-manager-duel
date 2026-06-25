package com.football.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateListingRequest {
    @NotNull(message = "Jucatorul este obligatoriu")
    public Long playerId;

    @NotNull(message = "Pretul cerut este obligatoriu")
    @Min(value = 1, message = "Pretul trebuie sa fie pozitiv")
    public BigDecimal askingPrice;
}
