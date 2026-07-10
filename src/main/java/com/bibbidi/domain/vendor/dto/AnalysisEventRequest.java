package com.bibbidi.domain.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record AnalysisEventRequest(
    @NotBlank
    @Size(max = 100)
    String title,
    @NotNull
    LocalDate date,
    LocalTime time,
    @NotBlank
    @Size(max = 10_000)
    String quote
) {
}
