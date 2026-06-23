package com.football.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data public class CastVoteRequest {
    @NotNull public String voteType;
}
