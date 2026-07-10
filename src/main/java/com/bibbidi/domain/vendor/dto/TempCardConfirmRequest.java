package com.bibbidi.domain.vendor.dto;

import jakarta.validation.constraints.NotBlank;

public record TempCardConfirmRequest(
    @NotBlank
    String action,
    String reason
) {
}
