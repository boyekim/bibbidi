package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorCategory;
import com.bibbidi.domain.vendor.VendorStatus;
import java.time.LocalDate;

public record VendorCardResponse(
    String id,
    String category,
    String categoryLabel,
    String vendorName,
    String status,
    boolean isCurrent,
    LocalDate contractDate,
    Long totalAmount,
    Long depositAmount,
    Long balanceAmount,
    LocalDate balanceDueDate
) {

    public static VendorCardResponse from(VendorCard vendorCard) {
        return new VendorCardResponse(
            String.valueOf(vendorCard.getId()),
            category(vendorCard.getCategory()),
            categoryLabel(vendorCard.getCategory()),
            vendorCard.getName(),
            status(vendorCard.getStatus()),
            vendorCard.isCurrent(),
            vendorCard.getContractDate(),
            vendorCard.getTotalAmount(),
            vendorCard.getDepositAmount(),
            vendorCard.getBalanceAmount(),
            vendorCard.getBalanceDueDate()
        );
    }

    private static String category(VendorCategory category) {
        return switch (category) {
            case WEDDING_HALL -> "hall";
            case STUDIO -> "studio";
            case DRESS -> "dress";
            case MAKEUP -> "makeup";
        };
    }

    private static String categoryLabel(VendorCategory category) {
        return switch (category) {
            case WEDDING_HALL -> "웨딩홀";
            case STUDIO -> "스튜디오";
            case DRESS -> "드레스";
            case MAKEUP -> "메이크업";
        };
    }

    private static String status(VendorStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "inProgress";
            case CANDIDATE -> "candidate";
            case SCHEDULED -> "scheduled";
            case CONTRACTED -> "contracted";
            case NEEDS_COORDINATION -> "coordinating";
        };
    }
}
