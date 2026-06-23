package com.football.dto;
import lombok.Data;
import java.util.List;
@Data public class FormationResponse {
    public Long id;
    public String name;
    public String description;
    public List<FormationPositionResponse> positions;
}
