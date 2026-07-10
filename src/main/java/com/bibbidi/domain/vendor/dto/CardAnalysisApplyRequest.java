package com.bibbidi.domain.vendor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CardAnalysisApplyRequest(
    @Size(max = 20)
    List<@Valid AnalysisEventRequest> events,
    @Size(max = 20)
    List<@Valid AnalysisPaymentRequest> payments
) {

    public CardAnalysisApplyRequest {
        events = emptyIfNull(events);
        payments = emptyIfNull(payments);
    }

    @AssertTrue(message = "반영할 분석 결과가 올바르지 않습니다.")
    public boolean isValidItemCount() {
        int itemCount = events.size() + payments.size();
        return itemCount > 0 && itemCount <= 20;
    }

    private static <T> List<T> emptyIfNull(List<T> values) {
        if (values == null) {
            return List.of();
        }
        return values;
    }
}
