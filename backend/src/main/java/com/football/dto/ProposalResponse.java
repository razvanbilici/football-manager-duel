package com.football.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data public class ProposalResponse {
    public Long id;
    public Long proposerId;
    public String proposerName;
    public PlayerResponse player;
    public Long fromUserTeamId;
    public String fromTeamName;
    public String toTeamName;
    public BigDecimal offeredPrice;
    public String status;
    public String createdAt;
}
