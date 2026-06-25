package com.football.controller;

import com.football.dto.CreatePlayerRequest;
import com.football.dto.PagedResponse;
import com.football.dto.PlayerResponse;
import com.football.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<PagedResponse<PlayerResponse>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(playerService.filter(
                q, position, minPrice, maxPrice, minAge, maxAge, available,
                page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getById(id));
    }

    @GetMapping("/recent")
    public ResponseEntity<java.util.List<PlayerResponse>> getRecent() {
        return ResponseEntity.ok(playerService.getRecentPlayers());
    }

    /** Admin only – create a new player in a default club */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody CreatePlayerRequest req) {
        return ResponseEntity.ok(playerService.create(req));
    }
}
