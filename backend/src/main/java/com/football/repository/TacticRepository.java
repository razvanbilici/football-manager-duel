package com.football.repository;

import com.football.entity.Tactic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacticRepository extends JpaRepository<Tactic, Long> {
    List<Tactic> findByStyle(Tactic.Style style);
}
