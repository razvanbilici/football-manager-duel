package com.football.dto;
import com.football.entity.Tactic;
import lombok.Data;
@Data public class TacticResponse {
    public Long id;
    public String details;
    public Tactic.Style style;
}
