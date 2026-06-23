package com.football.repository;

import com.football.entity.TeamFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamFormationRepository extends JpaRepository<TeamFormation, Long> {}
