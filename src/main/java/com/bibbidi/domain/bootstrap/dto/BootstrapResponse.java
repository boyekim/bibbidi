package com.bibbidi.domain.bootstrap.dto;

import java.util.List;

public record BootstrapResponse(
    CoupleResponse couple,
    List<VendorResponse> vendors,
    List<PaymentResponse> payments,
    List<EventItemResponse> events,
    List<CandidateResponse> candidates
) {
}
