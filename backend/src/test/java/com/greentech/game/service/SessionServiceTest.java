package com.greentech.game.service;

import com.greentech.game.domain.model.GameSession;
import com.greentech.game.domain.model.enums.SessionStatus;
import com.greentech.game.dto.request.CreateSessionRequest;
import com.greentech.game.dto.request.JoinSessionRequest;
import com.greentech.game.dto.response.SessionResponse;
import com.greentech.game.exception.IllegalGameStateException;
import com.greentech.game.repository.GameSessionRepository;
import com.greentech.game.repository.SessionPlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private GameSessionRepository sessionRepository;

    @Mock
    private SessionPlayerRepository playerRepository;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(sessionRepository, playerRepository);
    }

    @Test
    void createSession_returnsSessionWithCode() {
        CreateSessionRequest req = new CreateSessionRequest("Alice", 4, 8);

        when(sessionRepository.existsByCode(anyString())).thenReturn(false);
        when(sessionRepository.save(any(GameSession.class))).thenAnswer(inv -> {
            GameSession s = inv.getArgument(0);
            return s;
        });

        SessionResponse response = sessionService.createSession(req);

        assertThat(response.hostName()).isEqualTo("Alice");
        assertThat(response.code()).isNotBlank();
        assertThat(response.status()).isEqualTo(SessionStatus.WAITING);
    }

    @Test
    void joinSession_fullSession_throwsIllegalState() {
        GameSession session = new GameSession();
        session.setCode("ABC123");
        session.setStatus(SessionStatus.WAITING);
        session.setMaxPlayers(2);

        var players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            var p = new com.greentech.game.domain.model.SessionPlayer();
            p.setActive(true);
            players.add(p);
        }
        session.getPlayers().addAll((java.util.Collection) players);

        when(sessionRepository.findByCode("ABC123")).thenReturn(Optional.of(session));

        JoinSessionRequest req = new JoinSessionRequest("ABC123", "Charlie", "pid-3");

        assertThatThrownBy(() -> sessionService.joinSession(req))
            .isInstanceOf(IllegalGameStateException.class)
            .hasMessageContaining("full");
    }
}
