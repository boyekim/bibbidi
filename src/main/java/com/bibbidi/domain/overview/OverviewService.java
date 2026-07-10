package com.bibbidi.domain.overview;

import com.bibbidi.domain.overview.dto.OverviewCardResponse;
import com.bibbidi.domain.overview.dto.OverviewCoupleResponse;
import com.bibbidi.domain.overview.dto.OverviewEventResponse;
import com.bibbidi.domain.overview.dto.OverviewResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.DemoWeddingDataService;
import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.PaymentScheduleRepository;
import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorCardRepository;
import com.bibbidi.domain.vendor.VendorEvent;
import com.bibbidi.domain.vendor.VendorEventRepository;
import com.bibbidi.domain.wedding.WeddingProfile;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OverviewService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final VendorCardRepository vendorCardRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final VendorEventRepository vendorEventRepository;

    @Transactional
    public OverviewResponse getOverview(User user) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        List<PaymentSchedule> paymentSchedules = paymentScheduleRepository.findByVendorCardInOrderByDueDateAscIdAsc(vendorCards);
        List<VendorEvent> vendorEvents = vendorEventRepository.findByVendorCardInOrderByEventAtAscIdAsc(vendorCards);

        long spent = paymentSchedules.stream()
            .filter(PaymentSchedule::isPaid)
            .map(PaymentSchedule::getAmount)
            .filter(amount -> amount != null)
            .mapToLong(Long::longValue)
            .sum();

        return new OverviewResponse(
            OverviewCoupleResponse.from(weddingProfile, user, spent),
            vendorCards.stream()
                .map(OverviewCardResponse::from)
                .toList(),
            upcomingEvents(vendorEvents, paymentSchedules)
        );
    }

    private List<OverviewEventResponse> upcomingEvents(
        List<VendorEvent> vendorEvents,
        List<PaymentSchedule> paymentSchedules
    ) {
        LocalDate today = LocalDate.now();
        List<OverviewEventResponse> generalEvents = vendorEvents.stream()
            .map(OverviewEventResponse::from)
            .toList();
        List<OverviewEventResponse> paymentEvents = paymentSchedules.stream()
            .filter(paymentSchedule -> paymentSchedule.getDueDate() != null)
            .map(OverviewEventResponse::from)
            .toList();

        return java.util.stream.Stream.concat(generalEvents.stream(), paymentEvents.stream())
            .filter(event -> !event.date().isBefore(today))
            .sorted(Comparator
                .comparing(OverviewEventResponse::date)
                .thenComparing(event -> event.time() == null ? "" : event.time())
                .thenComparing(OverviewEventResponse::id))
            .limit(3)
            .toList();
    }
}
