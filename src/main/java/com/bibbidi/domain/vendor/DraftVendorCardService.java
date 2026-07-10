package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.DraftVendorCardRequest;
import com.bibbidi.domain.vendor.dto.DraftVendorCardResponse;
import com.bibbidi.domain.wedding.WeddingProfile;
import com.bibbidi.support.exception.BadRequestException;
import com.bibbidi.support.exception.NotFoundException;
import com.bibbidi.support.exception.errors.VendorErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DraftVendorCardService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final DraftVendorCardRepository draftVendorCardRepository;

    @Transactional
    public DraftVendorCardResponse create(User user, DraftVendorCardRequest request) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        DraftVendorCard draft = new DraftVendorCard(
            weddingProfile,
            status(request.status()),
            request.sourceMessage()
        );
        update(draft, request);
        return DraftVendorCardResponse.from(draftVendorCardRepository.save(draft));
    }

    @Transactional
    public DraftVendorCardResponse update(User user, Long draftId, DraftVendorCardRequest request) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        DraftVendorCard draft = findOwnedDraft(weddingProfile, draftId);
        update(draft, request);
        return DraftVendorCardResponse.from(draft);
    }

    private DraftVendorCard findOwnedDraft(WeddingProfile weddingProfile, Long draftId) {
        return draftVendorCardRepository.findByIdAndWeddingProfile(draftId, weddingProfile)
            .orElseThrow(() -> new NotFoundException(VendorErrors.DRAFT_CARD_NOT_FOUND));
    }

    private void update(DraftVendorCard draft, DraftVendorCardRequest request) {
        validateAmounts(request);
        draft.updateVendorInfo(category(request.category()), request.name().trim(), status(request.status()));
        draft.updateContract(request.contractDate(), request.totalAmount(), request.depositAmount(),
            request.balanceAmount(), request.balanceDueDate());
        draft.updateMemo(request.memo());
        draft.updateSourceMessage(request.sourceMessage());
        draft.updateSchedule(request.scheduleDate(), request.scheduleTime(), request.scheduleTitle());
    }

    private VendorCategory category(String value) {
        return VendorCategory.from(value)
            .orElseThrow(() -> new BadRequestException(VendorErrors.INVALID_DRAFT_CARD));
    }

    private VendorStatus status(String value) {
        return VendorStatus.from(value)
            .orElseThrow(() -> new BadRequestException(VendorErrors.INVALID_DRAFT_CARD));
    }

    private void validateAmounts(DraftVendorCardRequest request) {
        if (exceedsTotal(request.depositAmount(), request.totalAmount())) {
            throw new BadRequestException(VendorErrors.INVALID_DRAFT_CARD);
        }
        if (exceedsTotal(request.balanceAmount(), request.totalAmount())) {
            throw new BadRequestException(VendorErrors.INVALID_DRAFT_CARD);
        }
    }

    private boolean exceedsTotal(Long amount, Long totalAmount) {
        return amount != null && totalAmount != null && amount > totalAmount;
    }
}
