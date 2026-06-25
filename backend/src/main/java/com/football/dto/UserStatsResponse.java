package com.football.dto;

import lombok.Data;

@Data
public class UserStatsResponse {
    public int totalTransfers;
    public double totalSpent;
    public double totalEarned;
    public int squadSize;
    public int playersOnPitch;
    public double squadTotalValue;
    public int teamVotes;
    public long teamDownVotes;
}
