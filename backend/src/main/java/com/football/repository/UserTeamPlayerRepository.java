package com.football.repository;

import com.football.entity.UserTeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTeamPlayerRepository extends JpaRepository<UserTeamPlayer, Long> {
    List<UserTeamPlayer> findByUserTeamId(Long userTeamId);
    Optional<UserTeamPlayer> findByUserTeamIdAndPlayerId(Long userTeamId, Long playerId);
    boolean existsByPlayerId(Long playerId); // check if player is in ANY user team
    boolean existsByUserTeamIdAndPlayerId(Long userTeamId, Long playerId);
    Optional<UserTeamPlayer> findByUserTeamIdAndSlotNumber(Long userTeamId, Integer slotNumber);
    long countByUserTeamIdAndSlotNumberBetween(Long userTeamId, int minSlot, int maxSlot);
}
