package com.football.service;

import com.football.dto.CreatePlayerRequest;
import com.football.dto.PagedResponse;
import com.football.dto.PlayerResponse;
import com.football.entity.Player;
import com.football.entity.PlayerTeam;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.PlayerRepository;
import com.football.repository.PlayerTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public java.util.List<PlayerResponse> getRecentPlayers() {
        return playerRepository.findTop8ByOrderByCreatedAtDesc().stream()
                .map(mapper::toPlayerResponse)
                .collect(Collectors.toList());
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

    public PagedResponse<PlayerResponse> filter(String q, String position,
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            Integer minAge, Integer maxAge, Boolean available,
            int page, int size, String sortBy, String sortDir) {

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

        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PlayerResponse> result = playerRepository.findAll(spec, pageable)
            .map(mapper::toPlayerResponse);
        return PagedResponse.of(result);
    }

    @Transactional
    public PlayerResponse update(Long id, CreatePlayerRequest req) {
        Player p = findById(id);
        if (req.getName() != null) p.setName(req.getName());
        if (req.getNickname() != null) p.setNickname(req.getNickname());
        if (req.getPosition() != null) p.setPosition(req.getPosition());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getAge() != null) p.setAge(req.getAge());
        if (req.getHeightCm() != null) p.setHeightCm(req.getHeightCm());
        if (req.getNationality() != null) p.setNationality(req.getNationality());
        if (req.getPreferredFoot() != null) p.setPreferredFoot(req.getPreferredFoot());
        if (req.getShirtNumber() != null) p.setShirtNumber(req.getShirtNumber());
        if (req.getAvailable() != null) p.setAvailable(req.getAvailable());
        return mapper.toPlayerResponse(playerRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + id));
    }
}
