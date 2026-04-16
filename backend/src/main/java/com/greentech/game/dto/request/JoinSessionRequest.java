package com.greentech.game.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinSessionRequest(
    @NotBlank String sessionCode,
    @NotBlank @Size(min = 2, max = 50) String playerName,
    @NotBlank String playerId
) {}
