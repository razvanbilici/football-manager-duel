package com.football.controller;

import com.football.dto.ChangePasswordRequest;
import com.football.dto.MessageResponse;
import com.football.dto.UpdateProfileRequest;
import com.football.dto.UserResponse;
import com.football.dto.UserStatsResponse;
import com.football.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Get the authenticated user's full profile */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userService.getProfile(ud.getUsername()));
    }

    /** Get any user's public profile */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    /** Set favourite default club */
    @PutMapping("/me/favourite-team/{playerTeamId}")
    public ResponseEntity<UserResponse> setFavourite(@PathVariable Long playerTeamId,
                                                      @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userService.setFavouriteTeam(ud.getUsername(), playerTeamId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userService.updateProfile(ud.getUsername(), req));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestBody ChangePasswordRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        userService.changePassword(ud.getUsername(), req);
        return ResponseEntity.ok(new MessageResponse("Parola schimbata cu succes"));
    }

    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsResponse> myStats(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userService.getStats(ud.getUsername()));
    }
}
