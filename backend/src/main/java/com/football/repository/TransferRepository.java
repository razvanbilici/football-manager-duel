package com.football.repository;

import com.football.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByToUserTeamIdOrderByDateDesc(Long userTeamId);
    List<Transfer> findByFromUserTeamIdOrderByDateDesc(Long userTeamId);
    List<Transfer> findTop20ByOrderByDateDesc();
}
