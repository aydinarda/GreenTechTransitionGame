package com.greentech.game.service;

import com.greentech.game.domain.model.GameSession;
import com.greentech.game.dto.response.PlayerStateResponse;
import com.greentech.game.dto.response.RoundResultResponse;
import com.greentech.game.dto.response.SessionResponse;
import com.greentech.game.exception.NotFoundException;
import com.greentech.game.repository.GameSessionRepository;
import com.greentech.game.repository.RoundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class QueryService {

    private final GameSessionRepository sessionRepository;
    private final RoundRepository roundRepository;

    public QueryService(GameSessionRepository sessionRepository,
                        RoundRepository roundRepository) {
        this.sessionRepository = sessionRepository;
        this.roundRepository = roundRepository;
    }

    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
            .map(SessionResponse::from).toList();
    }

    public List<PlayerStateResponse> getLeaderboard(String sessionCode) {
        GameSession session = sessionRepository.findByCode(sessionCode)
            .orElseThrow(() -> new NotFoundException("Session not found: " + sessionCode));

        return session.getPlayers().stream()
            .filter(p -> p.isActive())
            .sorted((a, b) -> b.getCumulativeReward().compareTo(a.getCumulativeReward()))
            .map(PlayerStateResponse::from)
            .toList();
    }

    public List<RoundResultResponse> getAllRoundResults(String sessionCode) {
        GameSession session = sessionRepository.findByCode(sessionCode)
            .orElseThrow(() -> new NotFoundException("Session not found: " + sessionCode));

        return roundRepository.findBySessionOrderByRoundNumberAsc(session).stream()
            .map(RoundResultResponse::from)
            .toList();
    }
}
