package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Unified vote table.
 * targetType + targetId identify what is being voted on:
 *   USER_TEAM   → id of UserTeam
 *   PLAYER_TEAM → id of PlayerTeam
 *   TRANSFER    → id of Transfer
 *   PROPOSAL    → id of TransferProposal
 */
@Entity
@Table(name = "vote",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"}))
@Data
@NoArgsConstructor
public class Vote {

    public enum TargetType { USER_TEAM, PLAYER_TEAM, TRANSFER, PROPOSAL }
    public enum VoteType   { UP, DOWN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
