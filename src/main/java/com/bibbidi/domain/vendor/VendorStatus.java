package com.bibbidi.domain.vendor;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public enum VendorStatus {

    IN_PROGRESS("drafting"),
    CANDIDATE("candidate"),
    SCHEDULED("scheduled"),
    CONTRACTED("contracted"),
    NEEDS_COORDINATION("coordinating");

    private static final Map<String, VendorStatus> STATUSES = Map.ofEntries(
        Map.entry("DRAFTING", IN_PROGRESS),
        Map.entry("INPROGRESS", IN_PROGRESS),
        Map.entry("IN_PROGRESS", IN_PROGRESS),
        Map.entry("CANDIDATE", CANDIDATE),
        Map.entry("SCHEDULED", SCHEDULED),
        Map.entry("CONTRACTED", CONTRACTED),
        Map.entry("COORDINATING", NEEDS_COORDINATION),
        Map.entry("NEEDS_COORDINATION", NEEDS_COORDINATION)
    );

    private final String apiValue;

    VendorStatus(String apiValue) {
        this.apiValue = apiValue;
    }

    public static Optional<VendorStatus> from(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(STATUSES.get(value.trim().toUpperCase(Locale.ROOT)));
    }

    public String apiValue() {
        return apiValue;
    }
}
