package com.greentech.game.dto.response;

import com.greentech.game.domain.model.GameSession;
import com.greentech.game.domain.model.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SessionResponse(
    Long id,
    String code,
    String hostName,
    SessionStatus status,
    int maxPlayers,
    int totalRounds,
    int currentRound,
    BigDecimal alpha,
    BigDecimal beta,
    Instant createdAt,
    List<PlayerStateResponse> players
) {
    public static SessionResponse from(GameSession s) {
        List<PlayerStateResponse> players = s.getPlayers().stream()
            .map(PlayerStateResponse::from).toList();
        return new SessionResponse(
            s.getId(), s.getCode(), s.getHostName(), s.getStatus(),
            s.getMaxPlayers(), s.getTotalRounds(), s.getCurrentRound(),
            s.getAlpha(), s.getBeta(), s.getCreatedAt(), players
        );
    }
}
