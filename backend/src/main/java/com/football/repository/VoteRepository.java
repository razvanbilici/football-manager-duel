package com.football.repository;

import com.football.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndTargetTypeAndTargetId(Long userId, Vote.TargetType targetType, Long targetId);
    long countByTargetTypeAndTargetIdAndVoteType(Vote.TargetType targetType, Long targetId, Vote.VoteType voteType);
    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, Vote.TargetType type, Long targetId);
}
