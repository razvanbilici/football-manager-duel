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
    public Integer age;
    public Integer heightCm;
    public String nationality;
    public String preferredFoot;
    public Integer shirtNumber;
    public Boolean available;
}
