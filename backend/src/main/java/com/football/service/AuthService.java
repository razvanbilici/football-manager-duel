package com.football.service;

import com.football.dto.AuthResponse;
import com.football.dto.LoginRequest;
import com.football.dto.RegisterRequest;
import com.football.entity.User;
import com.football.entity.UserTeam;
import com.football.exception.BadRequestException;
import com.football.repository.UserRepository;
import com.football.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("Registration failed: email already in use: {}", req.getEmail());
            throw new BadRequestException("Email already in use");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        // Create an empty user team automatically
        UserTeam team = new UserTeam();
        team.setName(req.getName() + "'s Team");
        user.setUserTeam(team);

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        return resp;
    }

    private static final long REMEMBER_ME_MS = 30L * 24 * 60 * 60 * 1000; // 30 days

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        log.info("User logged in: {}", user.getEmail());

        String token = req.isRememberMe()
                ? jwtUtil.generateToken(user.getEmail(), REMEMBER_ME_MS)
                : jwtUtil.generateToken(user.getEmail());
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        return resp;
    }
}
