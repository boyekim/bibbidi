package com.bibbidi.domain.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record DraftVendorCardRequest(
    @NotBlank
    String category,
    @NotBlank
    @Size(max = 100)
    String name,
    @NotBlank
    String status,
    LocalDate contractDate,
    @PositiveOrZero
    Long totalAmount,
    @PositiveOrZero
    Long depositAmount,
    @PositiveOrZero
    Long balanceAmount,
    LocalDate balanceDueDate,
    @Size(max = 10_000)
    String memo,
    @Size(max = 10_000)
    String sourceMessage,
    LocalDate scheduleDate,
    LocalTime scheduleTime,
    @Size(max = 100)
    String scheduleTitle
) {
}
