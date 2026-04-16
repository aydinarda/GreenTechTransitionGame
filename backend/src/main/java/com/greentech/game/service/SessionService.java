package com.greentech.game.service;

import com.greentech.game.domain.engine.ShareAssignmentService;
import com.greentech.game.domain.model.GameSession;
import com.greentech.game.domain.model.SessionPlayer;
import com.greentech.game.domain.model.enums.SessionStatus;
import com.greentech.game.dto.request.CreateSessionRequest;
import com.greentech.game.dto.request.JoinSessionRequest;
import com.greentech.game.dto.response.PlayerStateResponse;
import com.greentech.game.dto.response.SessionResponse;
import com.greentech.game.exception.IllegalGameStateException;
import com.greentech.game.exception.NotFoundException;
import com.greentech.game.exception.ValidationException;
import com.greentech.game.repository.GameSessionRepository;
import com.greentech.game.repository.SessionPlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessionService {

    private final GameSessionRepository sessionRepository;
    private final SessionPlayerRepository playerRepository;
    private final ShareAssignmentService shareAssignmentService;

    public SessionService(GameSessionRepository sessionRepository,
                          SessionPlayerRepository playerRepository,
                          ShareAssignmentService shareAssignmentService) {
        this.sessionRepository = sessionRepository;
        this.playerRepository = playerRepository;
        this.shareAssignmentService = shareAssignmentService;
    }

    public SessionResponse createSession(CreateSessionRequest req) {
        GameSession session = new GameSession();
        session.setCode(generateUniqueCode());
        session.setHostName(req.hostName());
        session.setMaxPlayers(req.maxPlayers());
        session.setTotalRounds(req.totalRounds());
        session.setAlpha(req.alpha());
        session.setBeta(req.beta());
        session = sessionRepository.save(session);
        return SessionResponse.from(session);
    }

    public SessionResponse joinSession(JoinSessionRequest req) {
        GameSession session = sessionRepository.findByCode(req.sessionCode())
            .orElseThrow(() -> new NotFoundException("Session not found: " + req.sessionCode()));

        if (session.getStatus() != SessionStatus.WAITING) {
            throw new IllegalGameStateException("Session is not accepting players");
        }

        long activeCount = session.getPlayers().stream().filter(SessionPlayer::isActive).count();
        if (activeCount >= session.getMaxPlayers()) {
            throw new IllegalGameStateException("Session is full");
        }

        if (playerRepository.existsBySessionAndPlayerId(session, req.playerId())) {
            throw new ValidationException("Player already joined");
        }

        SessionPlayer player = new SessionPlayer();
        player.setSession(session);
        player.setPlayerName(req.playerName());
        player.setPlayerId(req.playerId());
        playerRepository.save(player);

        return SessionResponse.from(sessionRepository.findById(session.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(String code) {
        GameSession session = sessionRepository.findByCode(code)
            .orElseThrow(() -> new NotFoundException("Session not found: " + code));
        return SessionResponse.from(session);
    }

    public SessionResponse startSession(String code) {
        GameSession session = sessionRepository.findByCode(code)
            .orElseThrow(() -> new NotFoundException("Session not found: " + code));

        if (session.getStatus() != SessionStatus.WAITING) {
            throw new IllegalGameStateException("Session already started");
        }

        List<SessionPlayer> activePlayers = session.getPlayers().stream()
            .filter(SessionPlayer::isActive).toList();
        if (activePlayers.size() < 2) {
            throw new IllegalGameStateException("Need at least 2 players to start");
        }

        // Assign equal initial market shares: s_i = 1/N for all i
        shareAssignmentService.assignEqualShares(activePlayers);
        session.setStatus(SessionStatus.IN_PROGRESS);
        return SessionResponse.from(sessionRepository.save(session));
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        } while (sessionRepository.existsByCode(code));
        return code;
    }
}
