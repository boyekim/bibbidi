package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.DraftVendorCard;

public record DraftVendorCardResponse(Long id) {

    public static DraftVendorCardResponse from(DraftVendorCard draftVendorCard) {
        return new DraftVendorCardResponse(draftVendorCard.getId());
    }
}
