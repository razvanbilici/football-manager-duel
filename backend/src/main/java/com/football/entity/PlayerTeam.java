package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Default football clubs (e.g. Barcelona, Arsenal).
 * Players belong to these teams by default; users buy/transfer from them.
 */
@Entity
@Table(name = "player_team")
@Data
@NoArgsConstructor
public class PlayerTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Competition trophies / stats
    private Integer ucl = 0;    // UEFA Champions League titles
    private Integer league = 0;
    private Integer cup = 0;

    // Community votes / likes
    @Column(nullable = false)
    private Integer votes = 0;

    @OneToMany(mappedBy = "playerTeam", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();
}
