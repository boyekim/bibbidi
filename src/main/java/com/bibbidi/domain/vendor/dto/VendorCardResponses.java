package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.VendorCard;
import java.util.List;

public record VendorCardResponses(
    List<VendorCardResponse> cards
) {

    public static VendorCardResponses from(List<VendorCard> vendorCards) {
        return new VendorCardResponses(
            vendorCards.stream()
                .map(VendorCardResponse::from)
                .toList()
        );
    }
}
