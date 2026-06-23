package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password; // BCrypt hash

    @Column(nullable = false)
    private Double budget = 2_000_000_000.0; // starting budget (2 billion)

    /** The squad this user manages */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_team_id", unique = true)
    private UserTeam userTeam;

    /** Favourite community club (optional) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favourite_player_team_id")
    private PlayerTeam favouritePlayerTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    public enum Role { USER, ADMIN }
}
