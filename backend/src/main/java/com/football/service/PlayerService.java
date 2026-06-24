package com.football.service;

import com.football.dto.CreatePlayerRequest;
import com.football.dto.PlayerResponse;
import com.football.entity.Player;
import com.football.entity.PlayerTeam;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.PlayerRepository;
import com.football.repository.PlayerTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerTeamRepository playerTeamRepository;
    private final MapperService mapper;

    public List<PlayerResponse> getAll() {
        log.debug("Fetching all players");
        return playerRepository.findAll().stream().map(mapper::toPlayerResponse).collect(Collectors.toList());
    }

    public PlayerResponse getById(Long id) {
        log.debug("Fetching player by id: {}", id);
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
        p.setAge(req.getAge());
        p.setHeightCm(req.getHeightCm());
        p.setNationality(req.getNationality());
        p.setPreferredFoot(req.getPreferredFoot());
        p.setShirtNumber(req.getShirtNumber());
        p.setAvailable(req.getAvailable() != null ? req.getAvailable() : true);
        p.setPlayerTeam(pt);
        Player saved = playerRepository.save(p);
        log.info("Player created: {} (team: {})", saved.getName(), pt.getName());
        return mapper.toPlayerResponse(saved);
    }

    public List<PlayerResponse> filter(String q, String position,
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            Integer minAge, Integer maxAge, Boolean available) {

        org.springframework.data.jpa.domain.Specification<com.football.entity.Player> spec =
            org.springframework.data.jpa.domain.Specification.where(null);

        if (q != null && !q.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("nickname")), "%" + q.toLowerCase() + "%")
            ));
        }
        if (position != null && !position.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("position"), position));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (minAge != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("age"), minAge));
        }
        if (maxAge != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("age"), maxAge));
        }
        if (available != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("available"), available));
        }

        return playerRepository.findAll(spec).stream()
            .map(mapper::toPlayerResponse)
            .collect(java.util.stream.Collectors.toList());
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + id));
    }
}
