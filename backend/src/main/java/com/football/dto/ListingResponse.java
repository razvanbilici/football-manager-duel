package com.football.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data public class ListingResponse {
    public Long id;
    public Long sellerId;
    public String sellerName;
    public Long userTeamId;
    public String userTeamName;
    public PlayerResponse player;
    public BigDecimal askingPrice;
    public String createdAt;
}
