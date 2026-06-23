package com.football.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
@Data public class CreateListingRequest {
    @NotNull public Long playerId;
    @NotNull public BigDecimal askingPrice;
}
