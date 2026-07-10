package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.VendorChangeHistory;
import java.time.LocalDate;

public record ChangeHistoryResponse(
    String id,
    String fromVendorName,
    String toVendorName,
    String reason,
    LocalDate changedAt
) {

    public static ChangeHistoryResponse from(VendorChangeHistory history) {
        return new ChangeHistoryResponse(
            String.valueOf(history.getId()),
            history.getPreviousVendorName(),
            history.getNewVendorName(),
            history.getReason(),
            history.getChangedAt().toLocalDate()
        );
    }
}
