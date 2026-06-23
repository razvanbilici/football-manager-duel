package com.football.dto;
import lombok.Data;
@Data public class VoteCountResponse {
    public long upVotes;
    public long downVotes;
    public String myVote;
}
