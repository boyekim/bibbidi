package com.bibbidi.domain.vendor.dto;

public record TempCardConfirmResponse(
    VendorCardResponse card,
    String reply
) {

    public static TempCardConfirmResponse of(VendorCardResponse card, String reply) {
        return new TempCardConfirmResponse(card, reply);
    }
}
