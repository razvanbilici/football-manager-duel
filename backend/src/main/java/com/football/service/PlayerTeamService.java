package com.football.service;

import com.football.dto.CreatePlayerTeamRequest;
import com.football.dto.PlayerTeamResponse;
import com.football.entity.PlayerTeam;
import com.football.exception.BadRequestException;
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
public class PlayerTeamService {

    private final PlayerTeamRepository playerTeamRepository;
    private final PlayerRepository playerRepository;
    private final MapperService mapper;

    public List<PlayerTeamResponse> getAll() {
        return playerTeamRepository.findAll().stream()
                .map(mapper::toPlayerTeamResponse).collect(Collectors.toList());
    }

    public PlayerTeamResponse getById(Long id) {
        PlayerTeamResponse r = mapper.toPlayerTeamResponse(find(id));
        r.setPlayers(playerRepository.findByPlayerTeamId(id).stream()
                .map(mapper::toPlayerResponse).collect(Collectors.toList()));
        return r;
    }

    @Transactional
    public PlayerTeamResponse create(CreatePlayerTeamRequest req) {
        if (playerTeamRepository.findByNameIgnoreCase(req.getName()).isPresent()) {
            throw new BadRequestException("Club already exists: " + req.getName());
        }
        PlayerTeam pt = new PlayerTeam();
        pt.setName(req.getName());
        pt.setUcl(req.getUcl() != null ? req.getUcl() : 0);
        pt.setLeague(req.getLeague() != null ? req.getLeague() : 0);
        pt.setCup(req.getCup() != null ? req.getCup() : 0);
        return mapper.toPlayerTeamResponse(playerTeamRepository.save(pt));
    }

    private PlayerTeam find(Long id) {
        return playerTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + id));
    }
}
