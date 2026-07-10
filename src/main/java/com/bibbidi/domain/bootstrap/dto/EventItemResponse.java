package com.bibbidi.domain.bootstrap.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.VendorEvent;
import java.time.LocalDate;

public record EventItemResponse(
    String id,
    String vendorId,
    LocalDate date,
    String time,
    String title,
    String kind,
    String detail
) {

    public static EventItemResponse from(VendorEvent vendorEvent) {
        return new EventItemResponse(
            "e-" + vendorEvent.getId(),
            String.valueOf(vendorEvent.getVendorCard().getId()),
            vendorEvent.getEventAt().toLocalDate(),
            vendorEvent.getEventAt().toLocalTime().toString(),
            vendorEvent.getTitle(),
            "general",
            vendorEvent.getVendorCard().getName() + " · " + vendorEvent.getEventAt().toLocalTime()
        );
    }

    public static EventItemResponse from(PaymentSchedule paymentSchedule) {
        return new EventItemResponse(
            "p-" + paymentSchedule.getId(),
            String.valueOf(paymentSchedule.getVendorCard().getId()),
            paymentSchedule.getDueDate(),
            null,
            paymentSchedule.getVendorCard().getName() + " " + paymentSchedule.getLabel() + " 납부",
            "payment",
            paymentSchedule.getAmount() + "원 · " + paymentSchedule.getLabel()
        );
    }
}
