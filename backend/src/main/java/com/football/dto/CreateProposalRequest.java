package com.football.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
@Data public class CreateProposalRequest {
    @NotNull public Long playerId;
    @NotNull public Long fromUserTeamId;
    @NotNull public BigDecimal offeredPrice;
}
