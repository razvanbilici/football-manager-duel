package com.football.dto;
import lombok.Data;
import java.util.List;
@Data public class UserTeamResponse {
    public Long id;
    public String name;
    public FormationResponse formation;
    public TacticResponse tactic;
    public boolean submitted;
    public int votes;
    public List<UserTeamPlayerResponse> players;
    public Long ownerId;
    public String ownerName;
}
