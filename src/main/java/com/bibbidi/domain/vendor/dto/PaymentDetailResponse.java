package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import java.time.LocalDate;

public record PaymentDetailResponse(
    String id,
    String label,
    Long amount,
    LocalDate dueDate,
    boolean paid,
    String paidBy,
    String memo
) {

    public static PaymentDetailResponse from(PaymentSchedule paymentSchedule) {
        return new PaymentDetailResponse(
            String.valueOf(paymentSchedule.getId()),
            paymentSchedule.getLabel(),
            paymentSchedule.getAmount(),
            paymentSchedule.getDueDate(),
            paymentSchedule.isPaid(),
            paymentSchedule.isPaid() ? "joint" : null,
            paymentSchedule.getMemo()
        );
    }
}
