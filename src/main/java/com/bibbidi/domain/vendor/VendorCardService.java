package com.bibbidi.domain.vendor;

import com.bibbidi.domain.chat.ChatService;
import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.CardEventResponse;
import com.bibbidi.domain.vendor.dto.TempCardConfirmRequest;
import com.bibbidi.domain.vendor.dto.TempCardConfirmResponse;
import com.bibbidi.domain.vendor.dto.VendorCardDetailResponse;
import com.bibbidi.domain.vendor.dto.VendorCardMemoUpdateRequest;
import com.bibbidi.domain.vendor.dto.VendorCardResponse;
import com.bibbidi.domain.vendor.dto.VendorCardResponses;
import com.bibbidi.domain.wedding.WeddingProfile;
import com.bibbidi.support.exception.BadRequestException;
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
    private final ChatService chatService;
    private final DraftVendorCardRepository draftVendorCardRepository;
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

    @Transactional
    public TempCardConfirmResponse confirmTempCard(User user, Long tempCardId, TempCardConfirmRequest request) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        DraftVendorCard draft = draftVendorCardRepository.findByIdAndWeddingProfile(tempCardId, weddingProfile)
            .orElseThrow(() -> new NotFoundException(VendorErrors.DRAFT_CARD_NOT_FOUND));

        String action = request.action();
        if ("cancel".equals(action)) {
            draftVendorCardRepository.delete(draft);
            String reply = "임시 카드 생성을 취소했어요.";
            chatService.recordAssistantText(weddingProfile, reply);
            return TempCardConfirmResponse.of(null, reply);
        }

        validateConfirmAction(action);
        validateDraft(draft);

        VendorCard currentCard = vendorCardRepository
            .findFirstByWeddingProfileAndCategoryAndCurrentTrueOrderByIdAsc(weddingProfile, draft.getCategory())
            .orElse(null);
        boolean addCandidate = "add-candidate".equals(action);
        boolean changeCurrent = "change".equals(action) && currentCard != null;
        boolean selectAsCurrent = changeCurrent || (!addCandidate && currentCard == null);
        VendorStatus status = addCandidate ? VendorStatus.CANDIDATE : draft.getStatus();

        if (changeCurrent) {
            currentCard.unselect();
        }

        VendorCard vendorCard = new VendorCard(
            weddingProfile,
            draft.getCategory(),
            draft.getName(),
            status,
            selectAsCurrent
        );
        vendorCard.updateContract(
            draft.getContractDate(),
            draft.getTotalAmount(),
            draft.getDepositAmount(),
            balanceAmount(draft),
            draft.getBalanceDueDate()
        );
        vendorCard.updateMemo(draft.getMemo());
        vendorCardRepository.save(vendorCard);

        savePaymentSchedules(weddingProfile, draft, vendorCard, status);
        if (changeCurrent) {
            vendorChangeHistoryRepository.save(new VendorChangeHistory(
                weddingProfile,
                draft.getCategory(),
                currentCard,
                vendorCard,
                request.reason()
            ));
        }

        draftVendorCardRepository.delete(draft);
        String reply = confirmReply(action, draft, currentCard, selectAsCurrent, request.reason());
        chatService.recordAssistantText(weddingProfile, reply);

        return TempCardConfirmResponse.of(
            VendorCardResponse.from(vendorCard),
            reply
        );
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

    private void validateConfirmAction(String action) {
        if (!List.of("register", "change", "add-candidate").contains(action)) {
            throw new BadRequestException(VendorErrors.INVALID_CONFIRM_ACTION);
        }
    }

    private void validateDraft(DraftVendorCard draft) {
        if (draft.getCategory() == null || draft.getName() == null || draft.getName().isBlank()) {
            throw new BadRequestException(VendorErrors.INVALID_DRAFT_CARD);
        }
    }

    private void savePaymentSchedules(
        WeddingProfile weddingProfile,
        DraftVendorCard draft,
        VendorCard vendorCard,
        VendorStatus status
    ) {
        if (draft.getDepositAmount() != null) {
            paymentScheduleRepository.save(new PaymentSchedule(
                vendorCard,
                "계약금",
                draft.getDepositAmount(),
                draft.getContractDate() == null ? LocalDate.now() : draft.getContractDate(),
                status == VendorStatus.CONTRACTED,
                null
            ));
        }

        Long balanceAmount = balanceAmount(draft);
        if (balanceAmount != null && balanceAmount > 0) {
            paymentScheduleRepository.save(new PaymentSchedule(
                vendorCard,
                "잔금",
                balanceAmount,
                balanceDueDate(weddingProfile, draft),
                false,
                null
            ));
        }
    }

    private Long balanceAmount(DraftVendorCard draft) {
        if (draft.getBalanceAmount() != null) {
            return draft.getBalanceAmount();
        }
        if (draft.getTotalAmount() == null || draft.getDepositAmount() == null) {
            return null;
        }
        return draft.getTotalAmount() - draft.getDepositAmount();
    }

    private LocalDate balanceDueDate(WeddingProfile weddingProfile, DraftVendorCard draft) {
        if (draft.getBalanceDueDate() != null) {
            return draft.getBalanceDueDate();
        }
        if (weddingProfile.getWeddingDate() != null) {
            return weddingProfile.getWeddingDate();
        }
        return LocalDate.now();
    }

    private String confirmReply(
        String action,
        DraftVendorCard draft,
        VendorCard currentCard,
        boolean selectedAsCurrent,
        String reason
    ) {
        String categoryLabel = categoryLabel(draft.getCategory());
        if ("change".equals(action) && currentCard != null) {
            String reply = categoryLabel + "을 " + currentCard.getName() + "에서 " + draft.getName() + "(으)로 변경했어요.";
            if (reason != null && !reason.isBlank()) {
                return reply + " 변경 사유는 \"" + reason + "\"로 기록했어요.";
            }
            return reply;
        }
        if ("add-candidate".equals(action)) {
            return draft.getName() + "을(를) " + categoryLabel + " 후보로 추가했어요.";
        }
        if (selectedAsCurrent) {
            return draft.getName() + "을(를) " + categoryLabel + " 업체로 등록했어요.";
        }
        return draft.getName() + "을(를) " + categoryLabel + " 후보 카드로 등록했어요.";
    }

    private String categoryLabel(VendorCategory category) {
        return switch (category) {
            case WEDDING_HALL -> "웨딩홀";
            case STUDIO -> "스튜디오";
            case DRESS -> "드레스";
            case MAKEUP -> "메이크업";
        };
    }
}
