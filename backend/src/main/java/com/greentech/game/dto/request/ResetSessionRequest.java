package com.greentech.game.dto.request;

import com.greentech.game.domain.model.enums.ResetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetSessionRequest(
    @NotBlank String adminKey,
    @NotNull ResetType resetType
) {}
