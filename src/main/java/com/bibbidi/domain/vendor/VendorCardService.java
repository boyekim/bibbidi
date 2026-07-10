package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.VendorCardDetailResponse;
import com.bibbidi.domain.vendor.dto.VendorCardResponses;
import com.bibbidi.domain.wedding.WeddingProfile;
import com.bibbidi.support.exception.NotFoundException;
import com.bibbidi.support.exception.errors.VendorErrors;
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
        VendorCard vendorCard = vendorCardRepository.findById(cardId)
            .filter(card -> card.getWeddingProfile().getId().equals(weddingProfile.getId()))
            .orElseThrow(() -> new NotFoundException(VendorErrors.CARD_NOT_FOUND));

        return VendorCardDetailResponse.of(
            vendorCard,
            paymentScheduleRepository.findByVendorCardOrderByDueDateAscIdAsc(vendorCard),
            vendorOptionRepository.findByVendorCardOrderByIdAsc(vendorCard),
            vendorChangeHistoryRepository.findByPreviousCardOrNewCardOrderByChangedAtDescIdDesc(vendorCard, vendorCard),
            vendorEventRepository.findByVendorCardOrderByEventAtAscIdAsc(vendorCard)
        );
    }
}
