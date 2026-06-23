package com.football.controller;

import com.football.dto.MessageResponse;
import com.football.dto.NotificationResponse;
import com.football.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(notificationService.getAll(ud.getUsername()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(ud.getUsername())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        notificationService.markRead(ud.getUsername(), id);
        return ResponseEntity.ok(new MessageResponse("Marked as read"));
    }
}
