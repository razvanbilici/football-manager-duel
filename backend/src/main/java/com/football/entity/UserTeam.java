package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A squad built by a user.
 */
@Entity
@Table(name = "user_team")
@Data
@NoArgsConstructor
public class UserTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private TeamFormation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tactic_id")
    private Tactic tactic;

    /** Whether the team has been published / submitted for community voting */
    @Column(nullable = false)
    private Boolean submitted = false;

    /** Cumulative like count (derived from UserTeamVoting rows) */
    @Column(nullable = false)
    private Integer votes = 0;

    @OneToMany(mappedBy = "userTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTeamPlayer> players = new ArrayList<>();

    @OneToOne(mappedBy = "userTeam", fetch = FetchType.LAZY)
    private User owner;
}
