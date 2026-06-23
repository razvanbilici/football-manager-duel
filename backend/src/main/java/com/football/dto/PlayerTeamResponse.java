package com.football.dto;
import lombok.Data;
import java.util.List;
@Data public class PlayerTeamResponse {
    public Long id;
    public String name;
    public int ucl;
    public int league;
    public int cup;
    public int votes;
    public List<PlayerResponse> players;
}
