package com.football.service;

import com.football.dto.CreateListingRequest;
import com.football.dto.CreateProposalRequest;
import com.football.dto.ListingResponse;
import com.football.dto.ProposalResponse;
import com.football.dto.TransferResponse;
import com.football.entity.*;
import com.football.exception.BadRequestException;
import com.football.exception.ForbiddenException;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.*;
import com.football.util.SquadSlots;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferProposalRepository proposalRepository;
    private final PlayerListingRepository listingRepository;
    private final PlayerRepository playerRepository;
    private final UserTeamPlayerRepository userTeamPlayerRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserRepository userRepository;
    private final MapperService mapper;
    private final UserService userService;
    private final NotificationService notificationService;

    // ─── Buy from default club ────────────────────────────────────────────────

    @Transactional
    public TransferResponse buyFromClub(Long playerId, String email) {
        User buyer = userService.getByEmail(email);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        if (player.getPlayerTeam() == null) {
            throw new BadRequestException("Player is not available in any club");
        }
        if (userTeamPlayerRepository.existsByPlayerId(playerId)) {
            throw new BadRequestException("Player is already owned by a user");
        }

        BigDecimal price = player.getPrice();
        if (buyer.getBudget() < price.doubleValue()) {
            log.warn("Insufficient budget for user {}: required {}, available {}",
                    email, price, buyer.getBudget());
            throw new BadRequestException("Insufficient budget");
        }

        UserTeam buyerTeam = buyer.getUserTeam();
        if (buyerTeam == null) throw new BadRequestException("You have no team yet");

        buyer.setBudget(buyer.getBudget() - price.doubleValue());
        userRepository.save(buyer);

        List<UserTeamPlayer> squad = userTeamPlayerRepository.findByUserTeamId(buyerTeam.getId());
        UserTeamPlayer utp = new UserTeamPlayer();
        utp.setUserTeam(buyerTeam);
        utp.setPlayer(player);
        utp.setSlotNumber(SquadSlots.nextBenchSlot(squad));
        userTeamPlayerRepository.save(utp);

        Transfer t = new Transfer();
        t.setPlayer(player);
        t.setFromPlayerTeam(player.getPlayerTeam());
        t.setToUserTeam(buyerTeam);
        t.setPrice(price);
        t.setType(Transfer.TransferType.BUY_FROM_CLUB);
        t.setDate(LocalDateTime.now());

        TransferResponse response = mapper.toTransferResponse(transferRepository.save(t));
        log.info("User {} bought player {} (id: {}) from club for {}", email, player.getName(), playerId, price);
        return response;
    }

    // ─── Listings (sell to market) ────────────────────────────────────────────

    @Transactional
    public ListingResponse createListing(CreateListingRequest req, String email) {
        User seller = userService.getByEmail(email);
        UserTeam team = seller.getUserTeam();
        if (team == null) throw new BadRequestException("You have no team");

        if (!userTeamPlayerRepository.existsByUserTeamIdAndPlayerId(team.getId(), req.getPlayerId())) {
            throw new BadRequestException("Player not in your squad");
        }

        listingRepository.findByPlayerIdAndActiveTrue(req.getPlayerId())
                .ifPresent(l -> {
                    log.warn("Player {} is already listed for sale", req.getPlayerId());
                    throw new BadRequestException("Player already listed for sale");
                });

        Player player = playerRepository.findById(req.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        userTeamPlayerRepository.findByUserTeamIdAndPlayerId(team.getId(), req.getPlayerId())
                .ifPresent(utp -> {
                    if (SquadSlots.isInLineup(utp.getSlotNumber())) {
                        List<UserTeamPlayer> squad = userTeamPlayerRepository.findByUserTeamId(team.getId());
                        utp.setSlotNumber(SquadSlots.nextBenchSlot(squad));
                        userTeamPlayerRepository.save(utp);
                    }
                });

        PlayerListing listing = new PlayerListing();
        listing.setSeller(seller);
        listing.setPlayer(player);
        listing.setUserTeam(team);
        listing.setAskingPrice(req.getAskingPrice());
        ListingResponse response = mapper.toListingResponse(listingRepository.save(listing));
        log.info("User {} listed player {} (id: {}) for sale at {}",
                email, player.getName(), player.getId(), req.getAskingPrice());
        return response;
    }

    @Transactional
    public void cancelListing(Long listingId, String email) {
        User seller = userService.getByEmail(email);
        PlayerListing listing = listingRepository.findByIdAndSellerId(listingId, seller.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        listing.setActive(false);
        listingRepository.save(listing);
    }

    public List<ListingResponse> getActiveListings() {
        return listingRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream().map(mapper::toListingResponse).collect(Collectors.toList());
    }

    public List<ListingResponse> getMyListings(String email) {
        User seller = userService.getByEmail(email);
        return listingRepository.findBySellerIdAndActiveTrueOrderByCreatedAtDesc(seller.getId())
                .stream().map(mapper::toListingResponse).collect(Collectors.toList());
    }

    // ─── User-to-user proposals ───────────────────────────────────────────────

    @Transactional
    public ProposalResponse createProposal(CreateProposalRequest req, String email) {
        User proposer = userService.getByEmail(email);

        Player player = playerRepository.findById(req.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        UserTeam fromTeam = findUserTeamById(req.getFromUserTeamId());
        UserTeam toTeam = proposer.getUserTeam();
        if (toTeam == null) throw new BadRequestException("You have no team yet");

        if (!userTeamPlayerRepository.existsByUserTeamIdAndPlayerId(fromTeam.getId(), player.getId())) {
            throw new BadRequestException("Player not in source team");
        }
        if (fromTeam.getId().equals(toTeam.getId())) {
            throw new BadRequestException("Cannot propose to buy from yourself");
        }
        if (proposer.getBudget() < req.getOfferedPrice().doubleValue()) {
            log.warn("Insufficient budget for proposal by user {}: required {}, available {}",
                    email, req.getOfferedPrice(), proposer.getBudget());
            throw new BadRequestException("Insufficient budget for this offer");
        }
        if (req.getOfferedPrice().doubleValue() <= 0) {
            throw new BadRequestException("Offer must be greater than zero");
        }

        TransferProposal p = new TransferProposal();
        p.setProposer(proposer);
        p.setPlayer(player);
        p.setFromUserTeam(fromTeam);
        p.setToUserTeam(toTeam);
        p.setOfferedPrice(req.getOfferedPrice());

        TransferProposal saved = proposalRepository.save(p);

        User seller = userRepository.findByUserTeam_Id(fromTeam.getId())
                .orElseThrow(() -> new BadRequestException("Seller not found"));
        notificationService.notifyTransferOffer(seller, saved);

        return mapper.toProposalResponse(saved);
    }

    @Transactional
    public ProposalResponse respondToProposal(Long proposalId, boolean accept, String email) {
        log.info("User {} {} proposal {}", email, accept ? "accepted" : "rejected", proposalId);

        TransferProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal not found"));

        if (proposal.getStatus() != TransferProposal.Status.PENDING) {
            throw new BadRequestException("Proposal already resolved");
        }

        User responder = userService.getByEmail(email);
        UserTeam responderTeam = responder.getUserTeam();
        if (responderTeam == null || !responderTeam.getId().equals(proposal.getFromUserTeam().getId())) {
            throw new ForbiddenException("You do not own the source team");
        }

        proposal.setResolvedAt(LocalDateTime.now());
        notificationService.markReadByReference(proposalId);

        if (!accept) {
            proposal.setStatus(TransferProposal.Status.REJECTED);
            return mapper.toProposalResponse(proposalRepository.save(proposal));
        }

        User proposer = proposal.getProposer();
        if (proposer.getBudget() < proposal.getOfferedPrice().doubleValue()) {
            log.warn("Proposer {} no longer has sufficient budget for proposal {}: required {}, available {}",
                    proposer.getEmail(), proposalId, proposal.getOfferedPrice(), proposer.getBudget());
            proposal.setStatus(TransferProposal.Status.REJECTED);
            proposalRepository.save(proposal);
            throw new BadRequestException("Proposer no longer has sufficient budget");
        }

        UserTeamPlayer utp = userTeamPlayerRepository
                .findByUserTeamIdAndPlayerId(proposal.getFromUserTeam().getId(), proposal.getPlayer().getId())
                .orElseThrow(() -> new BadRequestException("Player no longer in source team"));
        userTeamPlayerRepository.delete(utp);

        UserTeamPlayer newUtp = new UserTeamPlayer();
        newUtp.setUserTeam(proposal.getToUserTeam());
        newUtp.setPlayer(proposal.getPlayer());
        List<UserTeamPlayer> buyerSquad =
                userTeamPlayerRepository.findByUserTeamId(proposal.getToUserTeam().getId());
        newUtp.setSlotNumber(SquadSlots.nextBenchSlot(buyerSquad));
        userTeamPlayerRepository.save(newUtp);

        deactivateListingForPlayer(proposal.getPlayer().getId());

        proposer.setBudget(proposer.getBudget() - proposal.getOfferedPrice().doubleValue());
        responder.setBudget(responder.getBudget() + proposal.getOfferedPrice().doubleValue());
        userRepository.save(proposer);
        userRepository.save(responder);

        Transfer t = new Transfer();
        t.setPlayer(proposal.getPlayer());
        t.setFromUserTeam(proposal.getFromUserTeam());
        t.setToUserTeam(proposal.getToUserTeam());
        t.setPrice(proposal.getOfferedPrice());
        t.setType(Transfer.TransferType.USER_TO_USER);
        t.setDate(LocalDateTime.now());
        transferRepository.save(t);

        proposal.setStatus(TransferProposal.Status.ACCEPTED);
        return mapper.toProposalResponse(proposalRepository.save(proposal));
    }

    // ─── Queries ─────────────────────────────────────────────────────────────

    public List<TransferResponse> getRecent() {
        return transferRepository.findTop20ByOrderByDateDesc()
                .stream().map(mapper::toTransferResponse).collect(Collectors.toList());
    }

    public List<ProposalResponse> getIncomingProposals(String email) {
        User user = userService.getByEmail(email);
        if (user.getUserTeam() == null) return List.of();
        return proposalRepository
                .findByFromUserTeamIdAndStatus(user.getUserTeam().getId(), TransferProposal.Status.PENDING)
                .stream().map(mapper::toProposalResponse).collect(Collectors.toList());
    }

    public List<ProposalResponse> getSentProposals(String email) {
        User user = userService.getByEmail(email);
        return proposalRepository.findByProposerIdOrderByCreatedAtDesc(user.getId())
                .stream().map(mapper::toProposalResponse).collect(Collectors.toList());
    }

    private void deactivateListingForPlayer(Long playerId) {
        listingRepository.findByPlayerIdAndActiveTrue(playerId)
                .ifPresent(l -> {
                    l.setActive(false);
                    listingRepository.save(l);
                });
    }

    private UserTeam findUserTeamById(Long id) {
        return userTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserTeam not found: " + id));
    }
}
