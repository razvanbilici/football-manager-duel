package com.football.service;

import com.football.dto.CastVoteRequest;
import com.football.dto.VoteCountResponse;
import com.football.entity.*;
import com.football.exception.BadRequestException;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserTeamRepository userTeamRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final UserService userService;

    @Transactional
    public VoteCountResponse vote(Vote.TargetType targetType, Long targetId,
                                  CastVoteRequest req, String email) {
        validateTarget(targetType, targetId);

        User user = userService.getByEmail(email);
        Vote.VoteType voteType;
        try {
            voteType = Vote.VoteType.valueOf(req.getVoteType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("voteType must be UP or DOWN");
        }

        Optional<Vote> existing = voteRepository.findByUserIdAndTargetTypeAndTargetId(
                user.getId(), targetType, targetId);

        if (existing.isPresent()) {
            Vote v = existing.get();
            if (v.getVoteType() == voteType) {
                log.debug("User {} toggled off {} vote on {} {}", email, voteType, targetType, targetId);
                voteRepository.delete(v);
            } else {
                log.debug("User {} switched vote to {} on {} {}", email, voteType, targetType, targetId);
                v.setVoteType(voteType);
                voteRepository.save(v);
            }
        } else {
            log.debug("User {} cast {} vote on {} {}", email, voteType, targetType, targetId);
            Vote v = new Vote();
            v.setUser(user);
            v.setTargetType(targetType);
            v.setTargetId(targetId);
            v.setVoteType(voteType);
            voteRepository.save(v);
        }

        // Update denormalised votes counter on UserTeam
        if (targetType == Vote.TargetType.USER_TEAM) {
            userTeamRepository.findById(targetId).ifPresent(team -> {
                long ups = voteRepository.countByTargetTypeAndTargetIdAndVoteType(
                        Vote.TargetType.USER_TEAM, targetId, Vote.VoteType.UP);
                team.setVotes((int) ups);
                userTeamRepository.save(team);
            });
        }

        return getVotes(targetType, targetId, email);
    }

    public VoteCountResponse getVotes(Vote.TargetType targetType, Long targetId, String email) {
        VoteCountResponse r = new VoteCountResponse();
        r.setUpVotes(voteRepository.countByTargetTypeAndTargetIdAndVoteType(
                targetType, targetId, Vote.VoteType.UP));
        r.setDownVotes(voteRepository.countByTargetTypeAndTargetIdAndVoteType(
                targetType, targetId, Vote.VoteType.DOWN));

        if (email != null) {
            User user = userService.getByEmail(email);
            voteRepository.findByUserIdAndTargetTypeAndTargetId(user.getId(), targetType, targetId)
                    .ifPresent(v -> r.setMyVote(v.getVoteType().name()));
        }
        return r;
    }

    private void validateTarget(Vote.TargetType type, Long id) {
        switch (type) {
            case USER_TEAM -> { if (!userTeamRepository.existsById(id))
                throw new ResourceNotFoundException("UserTeam not found"); }
            case PLAYER_TEAM -> { if (!playerTeamRepository.existsById(id))
                throw new ResourceNotFoundException("PlayerTeam not found"); }
            default -> {} // Transfer / Proposal validation omitted for brevity
        }
    }
}
