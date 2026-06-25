package com.football.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class PlayerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getPlayers_publicEndpoint_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/players", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getPlayers_withPositionFilter_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/players?position=GK", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getPlayers_withPagination_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/players?page=0&size=5", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void adminEndpoint_withoutAuth_returns403() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/users", String.class);
        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }
}
