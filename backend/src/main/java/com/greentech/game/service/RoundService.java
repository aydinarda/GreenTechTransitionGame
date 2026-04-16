package com.greentech.game.service;

import com.greentech.game.domain.engine.GameEngine;
import com.greentech.game.domain.model.*;
import com.greentech.game.domain.model.enums.RoundStatus;
import com.greentech.game.domain.model.enums.SessionStatus;
import com.greentech.game.dto.request.SubmitActionRequest;
import com.greentech.game.dto.response.RoundResponse;
import com.greentech.game.dto.response.RoundResultResponse;
import com.greentech.game.exception.IllegalGameStateException;
import com.greentech.game.exception.NotFoundException;
import com.greentech.game.exception.ValidationException;
import com.greentech.game.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class RoundService {

    private final GameSessionRepository sessionRepository;
    private final SessionPlayerRepository playerRepository;
    private final RoundRepository roundRepository;
    private final RoundActionRepository actionRepository;
    private final GameEngine gameEngine;

    public RoundService(GameSessionRepository sessionRepository,
                        SessionPlayerRepository playerRepository,
                        RoundRepository roundRepository,
                        RoundActionRepository actionRepository,
                        GameEngine gameEngine) {
        this.sessionRepository = sessionRepository;
        this.playerRepository = playerRepository;
        this.roundRepository = roundRepository;
        this.actionRepository = actionRepository;
        this.gameEngine = gameEngine;
    }

    public RoundResponse openRound(String sessionCode) {
        GameSession session = findSession(sessionCode);
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new IllegalGameStateException("Session is not in progress");
        }

        roundRepository.findBySessionAndStatus(session, RoundStatus.COLLECTING_ACTIONS)
            .ifPresent(r -> { throw new IllegalGameStateException("A round is already open"); });

        if (session.getCurrentRound() >= session.getTotalRounds()) {
            throw new IllegalGameStateException("All rounds have been played");
        }

        Round round = new Round();
        round.setSession(session);
        round.setRoundNumber(session.getCurrentRound() + 1);
        round.setStatus(RoundStatus.COLLECTING_ACTIONS);
        return RoundResponse.from(roundRepository.save(round));
    }

    public RoundResponse submitAction(String sessionCode, Long roundId, SubmitActionRequest req) {
        GameSession session = findSession(sessionCode);
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new NotFoundException("Round not found: " + roundId));

        if (!round.getSession().getId().equals(session.getId())) {
            throw new ValidationException("Round does not belong to session");
        }
        if (round.getStatus() != RoundStatus.COLLECTING_ACTIONS) {
            throw new IllegalGameStateException("Round is not accepting actions");
        }

        SessionPlayer player = playerRepository.findBySessionAndPlayerId(session, req.playerId())
            .orElseThrow(() -> new NotFoundException("Player not found"));

        if (actionRepository.existsByRoundAndPlayer(round, player)) {
            throw new ValidationException("Action already submitted for this round");
        }

        // Enforce ai ∈ [0, si]
        BigDecimal si = player.getMarketShare();
        if (req.greenInvestment().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Investment must be non-negative");
        }
        if (req.greenInvestment().compareTo(si) > 0) {
            throw new ValidationException(
                "Investment cannot exceed your market share (" + si.toPlainString() + ")");
        }

        RoundAction action = new RoundAction();
        action.setRound(round);
        action.setPlayer(player);
        action.setGreenInvestment(req.greenInvestment());
        actionRepository.save(action);

        return RoundResponse.from(round);
    }

    public RoundResultResponse resolveRound(String sessionCode, Long roundId) {
        GameSession session = findSession(sessionCode);
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new NotFoundException("Round not found: " + roundId));

        if (!round.getSession().getId().equals(session.getId())) {
            throw new ValidationException("Round does not belong to session");
        }
        if (round.getStatus() != RoundStatus.COLLECTING_ACTIONS) {
            throw new IllegalGameStateException("Round cannot be resolved in its current state");
        }

        round.setStatus(RoundStatus.RESOLVING);
        gameEngine.resolveRound(round);
        roundRepository.save(round);
        sessionRepository.save(session);

        return RoundResultResponse.from(round);
    }

    @Transactional(readOnly = true)
    public List<RoundResponse> getRounds(String sessionCode) {
        GameSession session = findSession(sessionCode);
        return roundRepository.findBySessionOrderByRoundNumberAsc(session)
            .stream().map(RoundResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RoundResultResponse getRoundResult(String sessionCode, Long roundId) {
        GameSession session = findSession(sessionCode);
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new NotFoundException("Round not found: " + roundId));
        if (!round.getSession().getId().equals(session.getId())) {
            throw new ValidationException("Round does not belong to session");
        }
        return RoundResultResponse.from(round);
    }

    private GameSession findSession(String code) {
        return sessionRepository.findByCode(code)
            .orElseThrow(() -> new NotFoundException("Session not found: " + code));
    }
}
