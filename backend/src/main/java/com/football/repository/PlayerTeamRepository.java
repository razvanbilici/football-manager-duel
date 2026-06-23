package com.football.repository;

import com.football.entity.PlayerTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerTeamRepository extends JpaRepository<PlayerTeam, Long> {
    Optional<PlayerTeam> findByNameIgnoreCase(String name);
}
