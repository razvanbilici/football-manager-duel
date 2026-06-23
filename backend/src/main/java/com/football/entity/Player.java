package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "player")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String nickname;

    @Column(nullable = false)
    private String position; // GK, CB, LB, RB, CM, CAM, LW, RW, ST …

    /**
     * The default club this player belongs to (source of truth for "available to buy").
     * Null if the player has been fully transferred and no longer listed under a PlayerTeam.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_team_id")
    private PlayerTeam playerTeam;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}
