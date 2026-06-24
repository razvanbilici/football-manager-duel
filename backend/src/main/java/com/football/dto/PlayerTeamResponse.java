package com.football.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
public class PlayerTeamResponse {
    public Long id;
    public String name;
    public int ucl;
    public int league;
    public int cup;
    public int votes;
    public List<PlayerResponse> players;
}
