package com.greentech.game.dto.response;

import com.greentech.game.domain.model.Round;
import com.greentech.game.domain.model.enums.RoundStatus;

import java.time.Instant;

public record RoundResponse(
    Long id,
    int roundNumber,
    RoundStatus status,
    Instant startedAt,
    Instant resolvedAt,
    int actionCount
) {
    public static RoundResponse from(Round r) {
        return new RoundResponse(
            r.getId(), r.getRoundNumber(), r.getStatus(),
            r.getStartedAt(), r.getResolvedAt(), r.getActions().size()
        );
    }
}
