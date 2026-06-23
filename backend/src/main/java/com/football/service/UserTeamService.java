package com.football.service;

import com.football.dto.AddPlayerRequest;
import com.football.dto.UpdateUserTeamRequest;
import com.football.dto.UserTeamResponse;
import com.football.entity.*;
import com.football.exception.BadRequestException;
import com.football.exception.ForbiddenException;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.*;
import com.football.util.SquadSlots;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTeamService {

    private final UserTeamRepository userTeamRepository;
    private final UserTeamPlayerRepository userTeamPlayerRepository;
    private final TeamFormationRepository formationRepository;
    private final TacticRepository tacticRepository;
    private final MapperService mapper;
    private final UserService userService;

    public List<UserTeamResponse> getAllSubmitted() {
        return userTeamRepository.findAllSubmittedOrderByVotes()
                .stream().map(mapper::toUserTeamResponse).collect(Collectors.toList());
    }

    public UserTeamResponse getById(Long id) {
        return mapper.toUserTeamResponse(findById(id));
    }

    @Transactional
    public UserTeamResponse update(Long teamId, String email, UpdateUserTeamRequest req) {
        UserTeam team = findById(teamId);
        assertOwner(team, email);

        if (req.getName() != null) team.setName(req.getName());

        if (req.getFormationId() != null) {
            TeamFormation f = formationRepository.findById(req.getFormationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formation not found"));
            team.setFormation(f);
        }
        if (req.getTacticId() != null) {
            Tactic t = tacticRepository.findById(req.getTacticId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tactic not found"));
            team.setTactic(t);
        }
        return mapper.toUserTeamResponse(userTeamRepository.save(team));
    }

    /**
     * Assign an owned squad player to a formation slot (1..11).
     * Player must already be in the user's squad (purchased via transfer).
     */
    @Transactional
    public UserTeamResponse addPlayer(Long teamId, String email, AddPlayerRequest req) {
        UserTeam team = findById(teamId);
        assertOwner(team, email);

        if (req.getSlotNumber() == null
                || req.getSlotNumber() < SquadSlots.LINEUP_MIN
                || req.getSlotNumber() > SquadSlots.LINEUP_MAX) {
            throw new BadRequestException("Slot must be between 1 and 11");
        }

        UserTeamPlayer utp = userTeamPlayerRepository
                .findByUserTeamIdAndPlayerId(teamId, req.getPlayerId())
                .orElseThrow(() -> new BadRequestException(
                        "Player is not in your squad. Buy them from a club first."));

        if (team.getFormation() == null) {
            throw new BadRequestException("Save a formation before placing players on the pitch");
        }

        boolean validSlot = team.getFormation().getPositions().stream()
                .anyMatch(fp -> fp.getSlotNumber().equals(req.getSlotNumber()));
        if (!validSlot) {
            throw new BadRequestException("Invalid slot for current formation");
        }

        userTeamPlayerRepository.findByUserTeamIdAndSlotNumber(teamId, req.getSlotNumber())
                .ifPresent(occupied -> {
                    if (!occupied.getPlayer().getId().equals(req.getPlayerId())) {
                        List<UserTeamPlayer> currentSquad =
                                userTeamPlayerRepository.findByUserTeamId(teamId);
                        occupied.setSlotNumber(SquadSlots.nextBenchSlot(currentSquad));
                        userTeamPlayerRepository.save(occupied);
                    }
                });

        utp.setSlotNumber(req.getSlotNumber());
        userTeamPlayerRepository.save(utp);

        return mapper.toUserTeamResponse(findById(teamId));
    }

    /** Move player from lineup to bench (keeps ownership). */
    @Transactional
    public UserTeamResponse removePlayer(Long teamId, Long playerId, String email) {
        UserTeam team = findById(teamId);
        assertOwner(team, email);

        UserTeamPlayer utp = userTeamPlayerRepository
                .findByUserTeamIdAndPlayerId(teamId, playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not in your squad"));

        List<UserTeamPlayer> squad = userTeamPlayerRepository.findByUserTeamId(teamId);
        utp.setSlotNumber(SquadSlots.nextBenchSlot(squad));
        userTeamPlayerRepository.save(utp);

        return mapper.toUserTeamResponse(findById(teamId));
    }

    @Transactional
    public UserTeamResponse submit(Long teamId, String email) {
        UserTeam team = findById(teamId);
        assertOwner(team, email);

        long lineupCount = userTeamPlayerRepository
                .countByUserTeamIdAndSlotNumberBetween(teamId, SquadSlots.LINEUP_MIN, SquadSlots.LINEUP_MAX);
        if (lineupCount < 11) {
            throw new BadRequestException(
                    "Team must have 11 players assigned to formation slots to submit");
        }
        if (team.getFormation() == null) {
            throw new BadRequestException("Select a formation before submitting");
        }

        team.setSubmitted(true);
        return mapper.toUserTeamResponse(userTeamRepository.save(team));
    }

    public UserTeam findById(Long id) {
        return userTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserTeam not found: " + id));
    }

    private void assertOwner(UserTeam team, String email) {
        User owner = userService.getByEmail(email);
        if (owner.getUserTeam() == null || !owner.getUserTeam().getId().equals(team.getId())) {
            throw new ForbiddenException("You do not own this team");
        }
    }
}
