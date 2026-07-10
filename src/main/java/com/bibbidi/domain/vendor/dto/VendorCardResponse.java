package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.VendorCard;
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
            vendorCard.getCategory().apiValue(),
            vendorCard.getCategory().label(),
            vendorCard.getName(),
            vendorCard.getStatus().apiValue(),
            vendorCard.isCurrent(),
            vendorCard.getContractDate(),
            vendorCard.getTotalAmount(),
            vendorCard.getDepositAmount(),
            vendorCard.getBalanceAmount(),
            vendorCard.getBalanceDueDate()
        );
    }

}
