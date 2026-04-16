package com.greentech.game.dto.response;

import com.greentech.game.domain.model.SessionPlayer;

import java.math.BigDecimal;

public record PlayerStateResponse(
    Long id,
    String playerName,
    String playerId,
    BigDecimal marketShare,
    BigDecimal cumulativeReward,
    boolean active
) {
    public static PlayerStateResponse from(SessionPlayer p) {
        return new PlayerStateResponse(
            p.getId(), p.getPlayerName(), p.getPlayerId(),
            p.getMarketShare(), p.getCumulativeReward(), p.isActive()
        );
    }
}
