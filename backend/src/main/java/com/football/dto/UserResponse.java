package com.football.dto;
import lombok.Data;
@Data public class UserResponse {
    public Long id;
    public String name;
    public String email;
    public Double budget;
    public UserTeamResponse userTeam;
    public Long favouritePlayerTeamId;
    public String favouritePlayerTeamName;
}
