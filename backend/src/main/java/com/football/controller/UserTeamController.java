package com.football.controller;

import com.football.dto.AddPlayerRequest;
import com.football.dto.PagedResponse;
import com.football.dto.UpdateUserTeamRequest;
import com.football.dto.UserTeamResponse;
import com.football.service.UserTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-teams")
@RequiredArgsConstructor
public class UserTeamController {

    private final UserTeamService userTeamService;

    /** All publicly submitted teams, ranked by votes */
    @GetMapping
    public ResponseEntity<PagedResponse<UserTeamResponse>> getSubmitted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "votes") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(userTeamService.getAllSubmitted(page, size, sortBy, sortDir, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTeamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userTeamService.getById(id));
    }

    /** Update name / formation / tactic */
    @PatchMapping("/{id}")
    public ResponseEntity<UserTeamResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserTeamRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userTeamService.update(id, ud.getUsername(), req));
    }

    /** Add a player to a slot */
    @PostMapping("/{id}/players")
    public ResponseEntity<UserTeamResponse> addPlayer(
            @PathVariable Long id,
            @Valid @RequestBody AddPlayerRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userTeamService.addPlayer(id, ud.getUsername(), req));
    }

    /** Remove a player */
    @DeleteMapping("/{id}/players/{playerId}")
    public ResponseEntity<UserTeamResponse> removePlayer(
            @PathVariable Long id,
            @PathVariable Long playerId,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userTeamService.removePlayer(id, playerId, ud.getUsername()));
    }

    /** Publish team to the community */
    @PostMapping("/{id}/submit")
    public ResponseEntity<UserTeamResponse> submit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userTeamService.submit(id, ud.getUsername()));
    }
}
