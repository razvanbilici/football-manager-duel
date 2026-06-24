package com.football.service;

import com.football.dto.NotificationResponse;
import com.football.dto.ProposalResponse;
import com.football.entity.Notification;
import com.football.entity.TransferProposal;
import com.football.entity.User;
import com.football.exception.ForbiddenException;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.NotificationRepository;
import com.football.repository.TransferProposalRepository;
import com.football.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TransferProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final MapperService mapper;

    @Transactional
    public void notifyTransferOffer(User seller, TransferProposal proposal) {
        Notification n = new Notification();
        n.setRecipient(seller);
        n.setType(Notification.Type.TRANSFER_OFFER);
        n.setReferenceId(proposal.getId());
        n.setMessage(String.format("%s offered €%,.0f for %s",
                proposal.getProposer().getName(),
                proposal.getOfferedPrice().doubleValue(),
                proposal.getPlayer().getName()));
        notificationRepository.save(n);
        log.debug("Notification created for user {}: transfer offer on proposal {}",
                seller.getEmail(), proposal.getId());
    }

    public List<NotificationResponse> getAll(String email) {
        User user = findUser(email);
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public long getUnreadCount(String email) {
        User user = findUser(email);
        return notificationRepository.countByRecipientIdAndReadFalse(user.getId());
    }

    @Transactional
    public void markRead(String email, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        User user = findUser(email);
        if (!n.getRecipient().getId().equals(user.getId())) {
            throw new ForbiddenException("Not your notification");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markReadByReference(Long referenceId) {
        notificationRepository.findByReferenceIdAndType(referenceId, Notification.Type.TRANSFER_OFFER)
                .ifPresent(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setType(n.getType().name());
        r.setMessage(n.getMessage());
        r.setReferenceId(n.getReferenceId());
        r.setRead(n.getRead());
        r.setCreatedAt(n.getCreatedAt().toString());
        if (n.getReferenceId() != null && n.getType() == Notification.Type.TRANSFER_OFFER) {
            proposalRepository.findById(n.getReferenceId())
                    .ifPresent(p -> r.setProposal(mapper.toProposalResponse(p)));
        }
        return r;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
