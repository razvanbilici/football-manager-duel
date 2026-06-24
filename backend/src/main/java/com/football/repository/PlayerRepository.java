package com.football.repository;

import com.football.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Player> {
    List<Player> findByPlayerTeamId(Long teamId);

    @Query("SELECT p FROM Player p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.nickname) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Player> search(String q);

    List<Player> findByPosition(String position);
}
