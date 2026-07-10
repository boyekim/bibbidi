package com.bibbidi.domain.bootstrap.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import java.time.LocalDate;

public record PaymentResponse(
    String id,
    String vendorId,
    String label,
    Long amount,
    LocalDate dueDate,
    boolean paid,
    String paidBy,
    String memo
) {

    public static PaymentResponse from(PaymentSchedule paymentSchedule) {
        return new PaymentResponse(
            String.valueOf(paymentSchedule.getId()),
            String.valueOf(paymentSchedule.getVendorCard().getId()),
            paymentSchedule.getLabel(),
            paymentSchedule.getAmount(),
            paymentSchedule.getDueDate(),
            paymentSchedule.isPaid(),
            paymentSchedule.isPaid() ? "joint" : null,
            paymentSchedule.getMemo()
        );
    }
}
