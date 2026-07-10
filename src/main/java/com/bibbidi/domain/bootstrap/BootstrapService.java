package com.bibbidi.domain.bootstrap;

import com.bibbidi.domain.bootstrap.dto.BootstrapResponse;
import com.bibbidi.domain.bootstrap.dto.CandidateResponse;
import com.bibbidi.domain.bootstrap.dto.CoupleResponse;
import com.bibbidi.domain.bootstrap.dto.EventItemResponse;
import com.bibbidi.domain.bootstrap.dto.PaymentResponse;
import com.bibbidi.domain.bootstrap.dto.VendorResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.DemoWeddingDataService;
import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.PaymentScheduleRepository;
import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorCardRepository;
import com.bibbidi.domain.vendor.VendorEvent;
import com.bibbidi.domain.vendor.VendorEventRepository;
import com.bibbidi.domain.vendor.VendorOption;
import com.bibbidi.domain.vendor.VendorOptionRepository;
import com.bibbidi.domain.wedding.WeddingProfile;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BootstrapService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final VendorCardRepository vendorCardRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final VendorEventRepository vendorEventRepository;
    private final VendorOptionRepository vendorOptionRepository;

    @Transactional
    public BootstrapResponse bootstrap(User user) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        List<PaymentSchedule> paymentSchedules = paymentScheduleRepository.findByVendorCardInOrderByDueDateAscIdAsc(vendorCards);
        List<VendorEvent> vendorEvents = vendorEventRepository.findByVendorCardInOrderByEventAtAscIdAsc(vendorCards);
        List<VendorOption> vendorOptions = vendorOptionRepository.findByVendorCardInOrderByIdAsc(vendorCards);

        long spent = paymentSchedules.stream()
            .filter(PaymentSchedule::isPaid)
            .map(PaymentSchedule::getAmount)
            .filter(amount -> amount != null)
            .mapToLong(Long::longValue)
            .sum();

        List<EventItemResponse> events = mergeEvents(vendorEvents, paymentSchedules);

        return new BootstrapResponse(
            CoupleResponse.from(weddingProfile, user, spent),
            vendorCards.stream().map(VendorResponse::from).toList(),
            paymentSchedules.stream().map(PaymentResponse::from).toList(),
            events,
            vendorOptions.stream().map(CandidateResponse::from).toList()
        );
    }

    private List<EventItemResponse> mergeEvents(
        List<VendorEvent> vendorEvents,
        List<PaymentSchedule> paymentSchedules
    ) {
        List<EventItemResponse> generalEvents = vendorEvents.stream()
            .map(EventItemResponse::from)
            .toList();
        List<EventItemResponse> paymentEvents = paymentSchedules.stream()
            .filter(paymentSchedule -> paymentSchedule.getDueDate() != null)
            .map(EventItemResponse::from)
            .toList();

        return java.util.stream.Stream.concat(generalEvents.stream(), paymentEvents.stream())
            .sorted(Comparator
                .comparing(EventItemResponse::date)
                .thenComparing(event -> event.time() == null ? "" : event.time())
                .thenComparing(EventItemResponse::id))
            .toList();
    }
}
