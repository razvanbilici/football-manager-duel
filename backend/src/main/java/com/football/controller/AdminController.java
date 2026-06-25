package com.football.controller;

import com.football.dto.CreatePlayerRequest;
import com.football.dto.CreatePlayerTeamRequest;
import com.football.dto.PlayerResponse;
import com.football.dto.PlayerTeamResponse;
import com.football.dto.UserResponse;
import com.football.repository.UserRepository;
import com.football.service.MapperService;
import com.football.service.PlayerService;
import com.football.service.PlayerTeamService;
import com.football.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final PlayerService playerService;
    private final PlayerTeamService playerTeamService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MapperService mapper;

    @PostMapping("/players")
    public ResponseEntity<PlayerResponse> createPlayer(@RequestBody CreatePlayerRequest req) {
        return ResponseEntity.ok(playerService.create(req));
    }

    @PatchMapping("/players/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id,
                                                        @RequestBody CreatePlayerRequest req) {
        return ResponseEntity.ok(playerService.update(id, req));
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clubs")
    public ResponseEntity<PlayerTeamResponse> createClub(@RequestBody CreatePlayerTeamRequest req) {
        return ResponseEntity.ok(playerTeamService.create(req));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .map(mapper::toUserResponse).collect(Collectors.toList()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> setRole(@PathVariable Long id,
                                                 @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(userService.setRole(id, body.get("role")));
    }
}
