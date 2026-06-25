package com.football.controller;

import com.football.dto.CreateListingRequest;
import com.football.dto.CreateProposalRequest;
import com.football.dto.ListingResponse;
import com.football.dto.PagedResponse;
import com.football.dto.ProposalResponse;
import com.football.dto.TransferResponse;
import com.football.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    /** 20 most recent completed transfers */
    @GetMapping
    public ResponseEntity<List<TransferResponse>> getRecent() {
        return ResponseEntity.ok(transferService.getRecent());
    }

    /** Buy a player directly from a default club */
    @PostMapping("/buy/{playerId}")
    public ResponseEntity<TransferResponse> buyFromClub(
            @PathVariable Long playerId,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.buyFromClub(playerId, ud.getUsername()));
    }

    // ─── Listings ─────────────────────────────────────────────────────────────

    @GetMapping("/listings")
    public ResponseEntity<PagedResponse<ListingResponse>> getListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transferService.getListingsPaged(page, size));
    }

    @GetMapping("/listings/mine")
    public ResponseEntity<List<ListingResponse>> getMyListings(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.getMyListings(ud.getUsername()));
    }

    @PostMapping("/listings")
    public ResponseEntity<ListingResponse> createListing(
            @Valid @RequestBody CreateListingRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.createListing(req, ud.getUsername()));
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<Void> cancelListing(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        transferService.cancelListing(id, ud.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ─── Proposals ──────────────────────────────────────────────────────────

    @PostMapping("/proposals")
    public ResponseEntity<ProposalResponse> createProposal(
            @Valid @RequestBody CreateProposalRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.createProposal(req, ud.getUsername()));
    }

    @GetMapping("/proposals/incoming")
    public ResponseEntity<List<ProposalResponse>> incoming(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.getIncomingProposals(ud.getUsername()));
    }

    @GetMapping("/proposals/sent")
    public ResponseEntity<List<ProposalResponse>> sent(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.getSentProposals(ud.getUsername()));
    }

    @PatchMapping("/proposals/{id}/accept")
    public ResponseEntity<ProposalResponse> accept(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.respondToProposal(id, true, ud.getUsername()));
    }

    @PatchMapping("/proposals/{id}/reject")
    public ResponseEntity<ProposalResponse> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transferService.respondToProposal(id, false, ud.getUsername()));
    }
}
