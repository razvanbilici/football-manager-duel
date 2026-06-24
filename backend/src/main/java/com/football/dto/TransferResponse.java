package com.football.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
public class TransferResponse {
    public Long id;
    public PlayerResponse player;
    public String fromTeamName;
    public String toTeamName;
    public BigDecimal price;
    public String date;
    public String type;
}
