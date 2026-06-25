package com.football.service;

import com.football.dto.CreatePlayerRequest;
import com.football.dto.PagedResponse;
import com.football.dto.PlayerResponse;
import com.football.entity.Player;
import com.football.exception.ResourceNotFoundException;
import com.football.repository.PlayerRepository;
import com.football.repository.PlayerTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerTeamRepository playerTeamRepository;

    @Mock
    private MapperService mapper;

    @InjectMocks
    private PlayerService playerService;

    private Player player;
    private PlayerResponse playerResponse;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1L);
        player.setName("Lionel Messi");
        player.setPosition("ST");
        player.setPrice(BigDecimal.valueOf(100_000_000));
        player.setAvailable(true);

        playerResponse = new PlayerResponse();
        playerResponse.setId(1L);
        playerResponse.setName("Lionel Messi");
        playerResponse.setPosition("ST");
    }

    @Test
    void filter_withNoParams_returnsAllPlayers() {
        Page<Player> page = new PageImpl<>(List.of(player));
        when(playerRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);
        when(mapper.toPlayerResponse(player)).thenReturn(playerResponse);

        PagedResponse<PlayerResponse> result = playerService.filter(
                null, null, null, null, null, null, null,
                0, 12, "name", "asc");

        assertThat(result.content).hasSize(1);
        assertThat(result.content.get(0).getName()).isEqualTo("Lionel Messi");
    }

    @Test
    void filter_withPositionParam_returnsEmptyPage() {
        when(playerRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        PagedResponse<PlayerResponse> result = playerService.filter(
                null, "GK", null, null, null, null, null,
                0, 12, "name", "asc");

        assertThat(result.content).isEmpty();
    }

    @Test
    void update_nonExistentPlayer_throwsResourceNotFoundException() {
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.update(999L, new CreatePlayerRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
