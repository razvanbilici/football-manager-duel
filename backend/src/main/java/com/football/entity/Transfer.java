package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Records a completed transfer of a player between two user teams,
 * or from a default PlayerTeam to a UserTeam.
 */
@Entity
@Table(name = "transfer")
@Data
@NoArgsConstructor
public class Transfer {

    public enum TransferType {
        BUY_FROM_CLUB,       // UserTeam ← PlayerTeam
        USER_TO_USER         // UserTeam ← UserTeam
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /** Null when buying directly from a default club */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_team_id")
    private UserTeam fromUserTeam;

    /** Null when selling back (future feature) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_team_id", nullable = false)
    private UserTeam toUserTeam;

    /** The default club source if type = BUY_FROM_CLUB */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_player_team_id")
    private PlayerTeam fromPlayerTeam;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferType type;
}
