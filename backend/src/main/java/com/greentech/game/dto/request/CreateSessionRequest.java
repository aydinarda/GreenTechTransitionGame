package com.greentech.game.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateSessionRequest(
    @NotBlank @Size(min = 2, max = 50) String hostName,
    @Min(2) @Max(10) int maxPlayers,
    @Min(1) @Max(20) int totalRounds,
    // α ∈ [0,1]: N- loss weight — 0=by share, 1=by log-deficit
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal alpha,
    // β ∈ [0,1]: N+ gain weight — 0=by share, 1=by log-surplus
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal beta
) {}
