package com.bibbidi.domain.vendor;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public enum VendorCategory {

    WEDDING_HALL("hall", "웨딩홀"),
    STUDIO("studio", "스튜디오"),
    DRESS("dress", "드레스"),
    MAKEUP("makeup", "메이크업");

    private static final Map<String, VendorCategory> CATEGORIES = Map.ofEntries(
        Map.entry("HALL", WEDDING_HALL),
        Map.entry("WEDDING_HALL", WEDDING_HALL),
        Map.entry("STUDIO", STUDIO),
        Map.entry("DRESS", DRESS),
        Map.entry("MAKEUP", MAKEUP)
    );

    private final String apiValue;
    private final String label;

    VendorCategory(String apiValue, String label) {
        this.apiValue = apiValue;
        this.label = label;
    }

    public static Optional<VendorCategory> from(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CATEGORIES.get(value.trim().toUpperCase(Locale.ROOT)));
    }

    public String apiValue() {
        return apiValue;
    }

    public String label() {
        return label;
    }
}
