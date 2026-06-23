package com.football.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data public class TransferResponse {
    public Long id;
    public PlayerResponse player;
    public String fromTeamName;
    public String toTeamName;
    public BigDecimal price;
    public String date;
    public String type;
}
