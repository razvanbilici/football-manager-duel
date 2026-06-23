package com.football.repository;

import com.football.entity.FormationPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormationPositionRepository extends JpaRepository<FormationPosition, Long> {}
