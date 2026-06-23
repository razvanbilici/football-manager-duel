package com.football.controller;

import com.football.dto.CastVoteRequest;
import com.football.dto.VoteCountResponse;
import com.football.entity.Vote;
import com.football.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Unified voting endpoint.
 *
 * POST  /api/votes/{targetType}/{targetId}   → cast or toggle vote
 * GET   /api/votes/{targetType}/{targetId}   → get counts (+ my vote if authenticated)
 *
 * targetType values: USER_TEAM | PLAYER_TEAM | TRANSFER | PROPOSAL
 */
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{targetType}/{targetId}")
    public ResponseEntity<VoteCountResponse> vote(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @Valid @RequestBody CastVoteRequest req,
            @AuthenticationPrincipal UserDetails ud) {

        Vote.TargetType type = Vote.TargetType.valueOf(targetType.toUpperCase());
        return ResponseEntity.ok(voteService.vote(type, targetId, req, ud.getUsername()));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<VoteCountResponse> getVotes(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDetails ud) {

        Vote.TargetType type = Vote.TargetType.valueOf(targetType.toUpperCase());
        String email = ud != null ? ud.getUsername() : null;
        return ResponseEntity.ok(voteService.getVotes(type, targetId, email));
    }
}
