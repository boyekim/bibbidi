package com.bibbidi.domain.overview.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.VendorEvent;
import java.time.LocalDate;

public record OverviewEventResponse(
    String id,
    String cardId,
    LocalDate date,
    String time,
    String title,
    String kind,
    String detail
) {

    public static OverviewEventResponse from(VendorEvent vendorEvent) {
        return new OverviewEventResponse(
            "e-" + vendorEvent.getId(),
            String.valueOf(vendorEvent.getVendorCard().getId()),
            vendorEvent.getEventAt().toLocalDate(),
            vendorEvent.getEventAt().toLocalTime().toString(),
            vendorEvent.getTitle(),
            "general",
            vendorEvent.getVendorCard().getName() + " · " + vendorEvent.getEventAt().toLocalTime()
        );
    }

    public static OverviewEventResponse from(PaymentSchedule paymentSchedule) {
        return new OverviewEventResponse(
            "p-" + paymentSchedule.getId(),
            String.valueOf(paymentSchedule.getVendorCard().getId()),
            paymentSchedule.getDueDate(),
            null,
            paymentSchedule.getVendorCard().getName() + " " + paymentSchedule.getLabel() + " 납부",
            "payment",
            formatAmount(paymentSchedule.getAmount()) + " · " + paymentSchedule.getLabel()
        );
    }

    private static String formatAmount(Long amount) {
        if (amount == null) {
            return "금액 미입력";
        }

        return String.format("%,d원", amount);
    }
}
