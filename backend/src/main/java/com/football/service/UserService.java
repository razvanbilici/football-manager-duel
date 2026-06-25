package com.football.service;

import com.football.dto.ChangePasswordRequest;
import com.football.dto.UpdateProfileRequest;
import com.football.dto.UserResponse;
import com.football.dto.UserStatsResponse;
import com.football.entity.PlayerTeam;
import com.football.entity.Transfer;
import com.football.entity.User;
import com.football.entity.UserTeamPlayer;
import com.football.entity.Vote;
import com.football.exception.BadRequestException;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.PlayerTeamRepository;
import com.football.repository.TransferRepository;
import com.football.repository.UserRepository;
import com.football.repository.UserTeamPlayerRepository;
import com.football.repository.VoteRepository;
import com.football.util.SquadSlots;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final MapperService mapper;
    private final PasswordEncoder passwordEncoder;
    private final TransferRepository transferRepository;
    private final UserTeamPlayerRepository userTeamPlayerRepository;
    private final VoteRepository voteRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse getProfile(String email) {
        return mapper.toUserResponse(getByEmail(email));
    }

    public UserResponse getById(Long id) {
        return mapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional
    public UserResponse setFavouriteTeam(String email, Long playerTeamId) {
        User user = getByEmail(email);
        PlayerTeam pt = playerTeamRepository.findById(playerTeamId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        user.setFavouritePlayerTeam(pt);
        return mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest req) {
        User user = getByEmail(email);
        if (req.getName() != null) user.setName(req.getName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        return mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest req) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Parola curenta este incorecta");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse setRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        return mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getStats(String email) {
        User user = getByEmail(email);
        UserStatsResponse r = new UserStatsResponse();
        if (user.getUserTeam() == null) return r;

        Long teamId = user.getUserTeam().getId();

        List<Transfer> spent = transferRepository.findByToUserTeamIdOrderByDateDesc(teamId);
        List<Transfer> earned = transferRepository.findByFromUserTeamIdOrderByDateDesc(teamId);
        r.setTotalTransfers(spent.size() + earned.size());
        r.setTotalSpent(spent.stream().mapToDouble(t -> t.getPrice().doubleValue()).sum());
        r.setTotalEarned(earned.stream().mapToDouble(t -> t.getPrice().doubleValue()).sum());

        List<UserTeamPlayer> squad = userTeamPlayerRepository.findByUserTeamId(teamId);
        r.setSquadSize(squad.size());
        r.setPlayersOnPitch((int) squad.stream()
                .filter(utp -> SquadSlots.isInLineup(utp.getSlotNumber()))
                .count());
        r.setSquadTotalValue(squad.stream()
                .mapToDouble(utp -> utp.getPlayer().getPrice().doubleValue())
                .sum());

        r.setTeamVotes(user.getUserTeam().getVotes());
        r.setTeamDownVotes(voteRepository.countByTargetTypeAndTargetIdAndVoteType(
                Vote.TargetType.USER_TEAM, user.getUserTeam().getId(), Vote.VoteType.DOWN));
        return r;
    }
}
