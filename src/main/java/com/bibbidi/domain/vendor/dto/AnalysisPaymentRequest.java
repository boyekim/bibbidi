package com.bibbidi.domain.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AnalysisPaymentRequest(
    @NotBlank
    @Size(max = 50)
    String label,
    @NotNull
    @Positive
    Long amount,
    LocalDate dueDate,
    @NotBlank
    @Size(max = 10_000)
    String quote
) {
}
