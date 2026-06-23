package com.football.dto;
import lombok.Data;
@Data public class UserTeamPlayerResponse {
    public Long id;
    public Integer slotNumber;
    public PlayerResponse player;
}
