package com.bibbidi.domain.vendor.dto;

import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorEvent;
import com.bibbidi.domain.vendor.VendorOption;
import com.bibbidi.domain.vendor.VendorChangeHistory;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record VendorCardDetailResponse(
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
    LocalDate balanceDueDate,
    String memoDoc,
    List<PaymentDetailResponse> payments,
    List<CandidateDetailResponse> candidates,
    List<ChangeHistoryResponse> history,
    List<CardEventResponse> events
) {

    public static VendorCardDetailResponse of(
        VendorCard vendorCard,
        List<PaymentSchedule> payments,
        List<VendorOption> candidates,
        List<VendorChangeHistory> history,
        List<VendorEvent> events
    ) {
        return new VendorCardDetailResponse(
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
            vendorCard.getBalanceDueDate(),
            vendorCard.getMemo(),
            payments.stream().map(PaymentDetailResponse::from).toList(),
            candidates.stream().map(CandidateDetailResponse::from).toList(),
            history.stream().map(ChangeHistoryResponse::from).toList(),
            cardEvents(events, payments)
        );
    }

    private static List<CardEventResponse> cardEvents(
        List<VendorEvent> events,
        List<PaymentSchedule> payments
    ) {
        List<CardEventResponse> generalEvents = events.stream()
            .map(CardEventResponse::from)
            .toList();
        List<CardEventResponse> paymentEvents = payments.stream()
            .filter(payment -> payment.getDueDate() != null)
            .map(CardEventResponse::from)
            .toList();

        return java.util.stream.Stream.concat(generalEvents.stream(), paymentEvents.stream())
            .sorted(Comparator
                .comparing(CardEventResponse::date)
                .thenComparing(VendorCardDetailResponse::eventTime)
                .thenComparing(CardEventResponse::id))
            .toList();
    }

    private static String eventTime(CardEventResponse event) {
        if (event.time() == null) {
            return "";
        }
        return event.time();
    }
}
