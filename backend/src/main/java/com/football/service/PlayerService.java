package com.football.service;

import com.football.dto.CreatePlayerRequest;
import com.football.dto.PlayerResponse;
import com.football.entity.Player;
import com.football.entity.PlayerTeam;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.PlayerRepository;
import com.football.repository.PlayerTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final MapperService mapper;

    public List<PlayerResponse> getAll() {
        return playerRepository.findAll().stream().map(mapper::toPlayerResponse).collect(Collectors.toList());
    }

    public PlayerResponse getById(Long id) {
        return mapper.toPlayerResponse(findById(id));
    }

    public List<PlayerResponse> search(String q) {
        return playerRepository.search(q).stream().map(mapper::toPlayerResponse).collect(Collectors.toList());
    }

    public List<PlayerResponse> getByTeam(Long teamId) {
        return playerRepository.findByPlayerTeamId(teamId).stream()
                .map(mapper::toPlayerResponse).collect(Collectors.toList());
    }

    @Transactional
    public PlayerResponse create(CreatePlayerRequest req) {
        PlayerTeam pt = playerTeamRepository.findById(req.getPlayerTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        Player p = new Player();
        p.setName(req.getName());
        p.setNickname(req.getNickname());
        p.setPosition(req.getPosition());
        p.setPrice(req.getPrice());
        p.setPlayerTeam(pt);
        return mapper.toPlayerResponse(playerRepository.save(p));
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + id));
    }
}
