package com.greentech.game.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResolveRoundRequest(
    @NotBlank String adminKey
) {}
