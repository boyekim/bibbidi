package com.bibbidi.domain.bootstrap.dto;

import com.bibbidi.domain.vendor.VendorOption;
import com.bibbidi.domain.vendor.VendorOptionStatus;
import java.util.List;

public record CandidateResponse(
    String id,
    String vendorId,
    String name,
    Long extraCost,
    String memo,
    String status,
    List<String> likedBy,
    int commentCount
) {

    public static CandidateResponse from(VendorOption vendorOption) {
        return new CandidateResponse(
            String.valueOf(vendorOption.getId()),
            String.valueOf(vendorOption.getVendorCard().getId()),
            vendorOption.getName(),
            vendorOption.getExtraCost(),
            vendorOption.getMemo(),
            status(vendorOption.getStatus()),
            vendorOption.getStatus() == VendorOptionStatus.SELECTED ? List.of("a", "b") : List.of(),
            vendorOption.getStatus() == VendorOptionStatus.SELECTED ? 2 : 0
        );
    }

    private static String status(VendorOptionStatus status) {
        return switch (status) {
            case CANDIDATE -> "candidate";
            case SELECTED -> "decided";
            case EXCLUDED -> "rejected";
        };
    }
}
