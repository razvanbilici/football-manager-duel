package com.football.service;

import com.football.dto.*;
import com.football.entity.*;
import com.football.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapperService {

    private final PlayerRepository playerRepository;

    public PlayerResponse toPlayerResponse(Player p) {
        PlayerResponse r = new PlayerResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setNickname(p.getNickname());
        r.setPosition(p.getPosition());
        r.setPrice(p.getPrice());
        if (p.getPlayerTeam() != null) {
            r.setPlayerTeamId(p.getPlayerTeam().getId());
            r.setPlayerTeamName(p.getPlayerTeam().getName());
        }
        r.setAge(p.getAge());
        r.setHeightCm(p.getHeightCm());
        r.setNationality(p.getNationality());
        r.setPreferredFoot(p.getPreferredFoot());
        r.setShirtNumber(p.getShirtNumber());
        r.setAvailable(p.getAvailable() != null ? p.getAvailable() : true);
        if (p.getCreatedAt() != null) r.setCreatedAt(p.getCreatedAt().toString());
        return r;
    }

    public PlayerTeamResponse toPlayerTeamResponse(PlayerTeam pt) {
        PlayerTeamResponse r = new PlayerTeamResponse();
        r.setId(pt.getId());
        r.setName(pt.getName());
        r.setUcl(pt.getUcl());
        r.setLeague(pt.getLeague());
        r.setCup(pt.getCup());
        r.setVotes(pt.getVotes());
        r.setPlayerCount((int) playerRepository.countByPlayerTeamId(pt.getId()));
        return r;
    }

    public FormationPositionResponse toFormationPositionResponse(FormationPosition fp) {
        FormationPositionResponse r = new FormationPositionResponse();
        r.setId(fp.getId());
        r.setSlotNumber(fp.getSlotNumber());
        r.setPosition(fp.getPosition());
        return r;
    }

    public FormationResponse toFormationResponse(TeamFormation f) {
        FormationResponse r = new FormationResponse();
        r.setId(f.getId());
        r.setName(f.getName());
        r.setDescription(f.getDescription());
        r.setPositions(f.getPositions().stream()
                .map(this::toFormationPositionResponse)
                .collect(Collectors.toList()));
        return r;
    }

    public TacticResponse toTacticResponse(Tactic t) {
        TacticResponse r = new TacticResponse();
        r.setId(t.getId());
        r.setDetails(t.getDetails());
        r.setStyle(t.getStyle());
        return r;
    }

    public UserTeamPlayerResponse toUserTeamPlayerResponse(UserTeamPlayer utp) {
        UserTeamPlayerResponse r = new UserTeamPlayerResponse();
        r.setId(utp.getId());
        r.setSlotNumber(utp.getSlotNumber());
        r.setPlayer(toPlayerResponse(utp.getPlayer()));
        return r;
    }

    public UserTeamResponse toUserTeamResponse(UserTeam ut) {
        UserTeamResponse r = new UserTeamResponse();
        r.setId(ut.getId());
        r.setName(ut.getName());
        if (ut.getFormation() != null) r.setFormation(toFormationResponse(ut.getFormation()));
        if (ut.getTactic() != null) r.setTactic(toTacticResponse(ut.getTactic()));
        r.setSubmitted(ut.getSubmitted());
        r.setVotes(ut.getVotes());
        r.setPlayers(ut.getPlayers().stream()
                .map(this::toUserTeamPlayerResponse)
                .collect(Collectors.toList()));
        if (ut.getOwner() != null) {
            r.setOwnerId(ut.getOwner().getId());
            r.setOwnerName(ut.getOwner().getName());
        }
        return r;
    }

    public UserResponse toUserResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setBudget(u.getBudget());
        r.setRole(u.getRole().name());
        if (u.getUserTeam() != null) r.setUserTeam(toUserTeamResponse(u.getUserTeam()));
        if (u.getFavouritePlayerTeam() != null) {
            r.setFavouritePlayerTeamId(u.getFavouritePlayerTeam().getId());
            r.setFavouritePlayerTeamName(u.getFavouritePlayerTeam().getName());
        }
        return r;
    }

    public TransferResponse toTransferResponse(Transfer t) {
        TransferResponse r = new TransferResponse();
        r.setId(t.getId());
        r.setPlayer(toPlayerResponse(t.getPlayer()));
        r.setFromTeamName(t.getFromPlayerTeam() != null
                ? t.getFromPlayerTeam().getName()
                : (t.getFromUserTeam() != null ? t.getFromUserTeam().getName() : "?"));
        r.setToTeamName(t.getToUserTeam().getName());
        r.setPrice(t.getPrice());
        r.setDate(t.getDate().toString());
        r.setType(t.getType().name());
        return r;
    }

    public ProposalResponse toProposalResponse(TransferProposal tp) {
        ProposalResponse r = new ProposalResponse();
        r.setId(tp.getId());
        r.setProposerId(tp.getProposer().getId());
        r.setProposerName(tp.getProposer().getName());
        r.setPlayer(toPlayerResponse(tp.getPlayer()));
        r.setFromUserTeamId(tp.getFromUserTeam().getId());
        r.setFromTeamName(tp.getFromUserTeam().getName());
        r.setToTeamName(tp.getToUserTeam().getName());
        r.setOfferedPrice(tp.getOfferedPrice());
        r.setStatus(tp.getStatus().name());
        r.setCreatedAt(tp.getCreatedAt().toString());
        return r;
    }

    public ListingResponse toListingResponse(PlayerListing listing) {
        ListingResponse r = new ListingResponse();
        r.setId(listing.getId());
        r.setSellerId(listing.getSeller().getId());
        r.setSellerName(listing.getSeller().getName());
        r.setUserTeamId(listing.getUserTeam().getId());
        r.setUserTeamName(listing.getUserTeam().getName());
        r.setPlayer(toPlayerResponse(listing.getPlayer()));
        r.setAskingPrice(listing.getAskingPrice());
        r.setCreatedAt(listing.getCreatedAt().toString());
        return r;
    }
}
