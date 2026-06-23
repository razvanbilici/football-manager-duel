package com.football.repository;

import com.football.entity.TransferProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferProposalRepository extends JpaRepository<TransferProposal, Long> {
    // Proposals received by a team (owner must respond)
    List<TransferProposal> findByFromUserTeamIdAndStatus(Long teamId, TransferProposal.Status status);
    // Proposals sent by a user
    List<TransferProposal> findByProposerIdOrderByCreatedAtDesc(Long userId);
}
