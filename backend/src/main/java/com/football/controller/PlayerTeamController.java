package com.football.controller;

import com.football.dto.CreatePlayerTeamRequest;
import com.football.dto.PlayerTeamResponse;
import com.football.service.PlayerTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player-teams")
@RequiredArgsConstructor
public class PlayerTeamController {

    private final PlayerTeamService playerTeamService;

    @GetMapping
    public ResponseEntity<List<PlayerTeamResponse>> getAll() {
        return ResponseEntity.ok(playerTeamService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerTeamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playerTeamService.getById(id));
    }

    /** Admin only – seed a new default club */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlayerTeamResponse> create(@Valid @RequestBody CreatePlayerTeamRequest req) {
        return ResponseEntity.ok(playerTeamService.create(req));
    }
}
