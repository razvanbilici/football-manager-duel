package com.football.service;

import com.football.dto.UserResponse;
import com.football.entity.PlayerTeam;
import com.football.entity.User;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.PlayerTeamRepository;
import com.football.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final MapperService mapper;

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
}
