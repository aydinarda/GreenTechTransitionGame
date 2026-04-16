package com.greentech.game.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SubmitActionRequest(
    @NotBlank String playerId,
    // ai ∈ [0, si] — upper bound enforced in RoundService against player's current share
    @NotNull @DecimalMin("0.0") BigDecimal greenInvestment
) {}
