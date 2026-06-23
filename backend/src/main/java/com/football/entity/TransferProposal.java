package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A user proposes to buy a player currently owned by another user.
 * The owner can accept or reject.
 */
@Entity
@Table(name = "transfer_proposal")
@Data
@NoArgsConstructor
public class TransferProposal {

    public enum Status { PENDING, ACCEPTED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User who is offering to buy */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposer_id", nullable = false)
    private User proposer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /** Current owner's team */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_team_id", nullable = false)
    private UserTeam fromUserTeam;

    /** Proposer's team (destination) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_team_id", nullable = false)
    private UserTeam toUserTeam;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal offeredPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;
}
