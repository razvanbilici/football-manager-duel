package com.football.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data public class PlayerResponse {
    public Long id;
    public String name;
    public String nickname;
    public String position;
    public BigDecimal price;
    public Long playerTeamId;
    public String playerTeamName;
}
