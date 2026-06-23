package com.football.dto;
import lombok.Data;
@Data public class UpdateUserTeamRequest {
    public String name;
    public Long formationId;
    public Long tacticId;
}
