package com.bibbidi.domain.bootstrap.dto;

import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorCategory;
import com.bibbidi.domain.vendor.VendorStatus;
import java.util.List;

public record VendorResponse(
    String id,
    String name,
    String type,
    String status,
    Long totalAmount,
    String contact,
    String manager,
    List<String> notes,
    String memoDoc
) {

    public static VendorResponse from(VendorCard vendorCard) {
        return new VendorResponse(
            String.valueOf(vendorCard.getId()),
            vendorCard.getName(),
            type(vendorCard.getCategory()),
            status(vendorCard.getStatus()),
            vendorCard.getTotalAmount(),
            null,
            manager(vendorCard.getCategory()),
            notes(vendorCard),
            vendorCard.getMemo()
        );
    }

    private static String type(VendorCategory category) {
        return switch (category) {
            case WEDDING_HALL -> "venue";
            case STUDIO -> "studio";
            case DRESS -> "dress";
            case MAKEUP -> "makeup";
        };
    }

    private static String status(VendorStatus status) {
        return switch (status) {
            case CONTRACTED -> "contracted";
            case SCHEDULED, IN_PROGRESS -> "deposit";
            case CANDIDATE, NEEDS_COORDINATION -> "reviewing";
        };
    }

    private static String manager(VendorCategory category) {
        return switch (category) {
            case WEDDING_HALL -> "예약실장";
            case STUDIO -> "촬영실장";
            case DRESS -> "피팅매니저";
            case MAKEUP -> "원장";
        };
    }

    private static List<String> notes(VendorCard vendorCard) {
        if (vendorCard.getMemo() == null || vendorCard.getMemo().isBlank()) {
            return List.of();
        }

        return List.of(vendorCard.getMemo());
    }
}
