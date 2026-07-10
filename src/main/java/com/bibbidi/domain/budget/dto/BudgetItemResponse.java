package com.bibbidi.domain.budget.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.VendorCard;
import java.util.List;

public record BudgetItemResponse(
    String cardId,
    String categoryLabel,
    String vendorName,
    Long totalAmount,
    List<BudgetPaymentResponse> payments
) {

    public static BudgetItemResponse from(VendorCard vendorCard, List<PaymentSchedule> payments) {
        return new BudgetItemResponse(
            String.valueOf(vendorCard.getId()),
            vendorCard.getCategory().label(),
            vendorCard.getName(),
            vendorCard.getTotalAmount(),
            payments.stream().map(BudgetPaymentResponse::from).toList()
        );
    }
}
