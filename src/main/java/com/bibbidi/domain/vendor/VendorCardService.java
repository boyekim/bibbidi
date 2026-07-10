package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.CardEventResponse;
import com.bibbidi.domain.vendor.dto.VendorCardDetailResponse;
import com.bibbidi.domain.vendor.dto.VendorCardMemoUpdateRequest;
import com.bibbidi.domain.vendor.dto.VendorCardResponse;
import com.bibbidi.domain.vendor.dto.VendorCardResponses;
import com.bibbidi.domain.wedding.WeddingProfile;
import com.bibbidi.support.exception.NotFoundException;
import com.bibbidi.support.exception.errors.VendorErrors;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VendorCardService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final VendorCardRepository vendorCardRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final VendorEventRepository vendorEventRepository;
    private final VendorOptionRepository vendorOptionRepository;
    private final VendorChangeHistoryRepository vendorChangeHistoryRepository;

    @Transactional
    public VendorCardResponses getCards(User user) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);

        return VendorCardResponses.from(vendorCards);
    }

    @Transactional
    public VendorCardDetailResponse getCard(User user, Long cardId) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        VendorCard vendorCard = getOwnedCard(weddingProfile, cardId);

        return VendorCardDetailResponse.of(
            vendorCard,
            paymentScheduleRepository.findByVendorCardOrderByDueDateAscIdAsc(vendorCard),
            vendorOptionRepository.findByVendorCardOrderByIdAsc(vendorCard),
            vendorChangeHistoryRepository.findByPreviousCardOrNewCardOrderByChangedAtDescIdDesc(vendorCard, vendorCard),
            vendorEventRepository.findByVendorCardOrderByEventAtAscIdAsc(vendorCard)
        );
    }

    @Transactional
    public VendorCardResponse updateMemo(User user, Long cardId, VendorCardMemoUpdateRequest request) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        VendorCard vendorCard = getOwnedCard(weddingProfile, cardId);

        vendorCard.updateMemo(request.memoDoc());

        return VendorCardResponse.from(vendorCard);
    }

    @Transactional
    public List<CardEventResponse> getEvents(User user, LocalDate from, LocalDate to) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        List<VendorEvent> vendorEvents = vendorEventRepository.findByVendorCardInOrderByEventAtAscIdAsc(vendorCards);
        List<PaymentSchedule> paymentSchedules = paymentScheduleRepository.findByVendorCardInOrderByDueDateAscIdAsc(vendorCards);

        List<CardEventResponse> generalEvents = vendorEvents.stream()
            .map(CardEventResponse::from)
            .toList();
        List<CardEventResponse> paymentEvents = paymentSchedules.stream()
            .filter(paymentSchedule -> paymentSchedule.getDueDate() != null)
            .map(CardEventResponse::from)
            .toList();

        return java.util.stream.Stream.concat(generalEvents.stream(), paymentEvents.stream())
            .filter(event -> isWithin(event.date(), from, to))
            .sorted(Comparator
                .comparing(CardEventResponse::date)
                .thenComparing(event -> event.time() == null ? "" : event.time())
                .thenComparing(CardEventResponse::id))
            .toList();
    }

    private VendorCard getOwnedCard(WeddingProfile weddingProfile, Long cardId) {
        return vendorCardRepository.findById(cardId)
            .filter(card -> card.getWeddingProfile().getId().equals(weddingProfile.getId()))
            .orElseThrow(() -> new NotFoundException(VendorErrors.CARD_NOT_FOUND));
    }

    private boolean isWithin(LocalDate date, LocalDate from, LocalDate to) {
        return (from == null || !date.isBefore(from))
            && (to == null || !date.isAfter(to));
    }
}
