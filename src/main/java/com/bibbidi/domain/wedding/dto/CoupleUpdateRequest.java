package com.bibbidi.domain.wedding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CoupleUpdateRequest(
    @NotBlank
    @Size(max = 30)
    String nameA,
    @NotBlank
    @Size(max = 30)
    String nameB,
    @NotNull
    LocalDate weddingDate,
    @NotNull
    @PositiveOrZero
    Long totalBudget
) {
}
