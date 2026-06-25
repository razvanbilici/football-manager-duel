package com.football.service;

import com.football.dto.ChangePasswordRequest;
import com.football.dto.UpdateProfileRequest;
import com.football.dto.UserResponse;
import com.football.entity.User;
import com.football.exception.BadRequestException;
import com.football.repository.PlayerTeamRepository;
import com.football.repository.TransferRepository;
import com.football.repository.UserRepository;
import com.football.repository.UserTeamPlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerTeamRepository playerTeamRepository;

    @Mock
    private MapperService mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private UserTeamPlayerRepository userTeamPlayerRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setPassword("hashed");
        user.setBudget(2_000_000_000.0);
        user.setRole(User.Role.USER);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Test User");
        userResponse.setEmail("test@test.com");
    }

    @Test
    void getProfile_existingUser_returnsResponse() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getProfile("test@test.com");

        assertThat(result.getName()).isEqualTo("Test User");
    }

    @Test
    void changePassword_wrongCurrentPassword_throwsBadRequest() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("wrong");
        req.setNewPassword("newpass");

        assertThatThrownBy(() -> userService.changePassword("test@test.com", req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateProfile_updatesNameAndEmail() {
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("New Name");
        savedUser.setEmail("new@test.com");
        savedUser.setPassword("hashed");
        savedUser.setBudget(2_000_000_000.0);
        savedUser.setRole(User.Role.USER);

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("New Name");
        updatedResponse.setEmail("new@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(mapper.toUserResponse(savedUser)).thenReturn(updatedResponse);

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName("New Name");
        req.setEmail("new@test.com");

        UserResponse result = userService.updateProfile("test@test.com", req);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }
}
