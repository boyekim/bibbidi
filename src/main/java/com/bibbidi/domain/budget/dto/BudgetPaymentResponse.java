package com.bibbidi.domain.budget.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import java.time.LocalDate;

public record BudgetPaymentResponse(
    String id,
    String label,
    Long amount,
    LocalDate dueDate,
    boolean paid,
    String memo
) {

    public static BudgetPaymentResponse from(PaymentSchedule paymentSchedule) {
        return new BudgetPaymentResponse(
            String.valueOf(paymentSchedule.getId()),
            paymentSchedule.getLabel(),
            paymentSchedule.getAmount(),
            paymentSchedule.getDueDate(),
            paymentSchedule.isPaid(),
            paymentSchedule.getMemo()
        );
    }
}
