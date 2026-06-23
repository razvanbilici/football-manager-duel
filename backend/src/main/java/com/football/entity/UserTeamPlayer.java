package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps a player to a slot inside a user's team (1..11 according to formation).
 */
@Entity
@Table(name = "user_team_player",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_team_id", "slot_number"}),
                @UniqueConstraint(columnNames = {"user_team_id", "player_id"})
        })
@Data
@NoArgsConstructor
public class UserTeamPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_team_id", nullable = false)
    private UserTeam userTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /** 1..11 = lineup slot; 12+ = bench */
    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber;
}
