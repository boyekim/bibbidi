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
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        List<PaymentSchedule> paymentSchedules = paymentScheduleRepository
            .findByVendorCardInOrderByDueDateAscIdAsc(vendorCards);

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
                .thenComparing(this::eventTime)
                .thenComparing(CardEventResponse::id))
            .toList();
    }

    @Transactional
    public TempCardConfirmResponse confirmTempCard(User user, Long tempCardId, TempCardConfirmRequest request) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        DraftVendorCard draft = draftVendorCardRepository.findByIdAndWeddingProfile(tempCardId, weddingProfile)
            .orElseThrow(() -> new NotFoundException(VendorErrors.DRAFT_CARD_NOT_FOUND));
        if ("cancel".equals(request.action())) {
            return cancelConfirmation(weddingProfile, draft);
        }
        validateConfirmAction(request.action());
        validateDraft(draft);
        VendorCard currentCard = currentCard(weddingProfile, draft);
        VendorCard vendorCard = createVendorCard(weddingProfile, draft, currentCard, request.action());
        saveConfirmationDetails(weddingProfile, draft, currentCard, vendorCard, request);
        return completeConfirmation(weddingProfile, draft, currentCard, vendorCard, request);
    }

    private TempCardConfirmResponse cancelConfirmation(WeddingProfile weddingProfile, DraftVendorCard draft) {
        draftVendorCardRepository.delete(draft);
        String reply = "임시 카드 생성을 취소했어요.";
        chatService.recordAssistantText(weddingProfile, reply);
        return TempCardConfirmResponse.of(null, reply);
    }

    private VendorCard currentCard(WeddingProfile weddingProfile, DraftVendorCard draft) {
        return vendorCardRepository
            .findFirstByWeddingProfileAndCategoryAndCurrentTrueOrderByIdAsc(weddingProfile, draft.getCategory())
            .orElse(null);
    }

    private VendorCard createVendorCard(
        WeddingProfile weddingProfile,
        DraftVendorCard draft,
        VendorCard currentCard,
        String action
    ) {
        unselectCurrent(action, currentCard);
        VendorCard vendorCard = new VendorCard(weddingProfile, draft.getCategory(), draft.getName(),
            confirmedStatus(action, draft), selectsAsCurrent(action, currentCard));
        copyDraft(draft, vendorCard);
        return vendorCardRepository.save(vendorCard);
    }

    private void copyDraft(DraftVendorCard draft, VendorCard vendorCard) {
        vendorCard.updateContract(
            draft.getContractDate(),
            draft.getTotalAmount(),
            draft.getDepositAmount(),
            balanceAmount(draft),
            draft.getBalanceDueDate()
        );
        vendorCard.updateMemo(draft.getMemo());
    }

    private void saveConfirmationDetails(
        WeddingProfile weddingProfile,
        DraftVendorCard draft,
        VendorCard currentCard,
        VendorCard vendorCard,
        TempCardConfirmRequest request
    ) {
        savePaymentSchedules(weddingProfile, draft, vendorCard, vendorCard.getStatus());
        saveSchedule(draft, vendorCard);
        saveChangeHistory(weddingProfile, draft, currentCard, vendorCard, request);
    }

    private TempCardConfirmResponse completeConfirmation(
        WeddingProfile weddingProfile,
        DraftVendorCard draft,
        VendorCard currentCard,
        VendorCard vendorCard,
        TempCardConfirmRequest request
    ) {
        draftVendorCardRepository.delete(draft);
        String reply = confirmReply(request, draft, currentCard, vendorCard);
        chatService.recordAssistantText(weddingProfile, reply);
        return TempCardConfirmResponse.of(VendorCardResponse.from(vendorCard), reply);
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
        saveDepositSchedule(draft, vendorCard, status);
        saveBalanceSchedule(weddingProfile, draft, vendorCard);
    }

    private void saveDepositSchedule(DraftVendorCard draft, VendorCard vendorCard, VendorStatus status) {
        if (draft.getDepositAmount() == null) {
            return;
        }
        paymentScheduleRepository.save(depositSchedule(draft, vendorCard, status));
    }

    private PaymentSchedule depositSchedule(DraftVendorCard draft, VendorCard vendorCard, VendorStatus status) {
        return new PaymentSchedule(
            vendorCard,
            "계약금",
            draft.getDepositAmount(),
            depositDueDate(draft),
            status == VendorStatus.CONTRACTED,
            null
        );
    }

    private void saveBalanceSchedule(WeddingProfile weddingProfile, DraftVendorCard draft, VendorCard vendorCard) {
        Long balanceAmount = balanceAmount(draft);
        if (balanceAmount == null || balanceAmount <= 0) {
            return;
        }
        paymentScheduleRepository.save(balanceSchedule(weddingProfile, draft, vendorCard, balanceAmount));
    }

    private PaymentSchedule balanceSchedule(
        WeddingProfile weddingProfile,
        DraftVendorCard draft,
        VendorCard vendorCard,
        Long balanceAmount
    ) {
        return new PaymentSchedule(vendorCard, "잔금", balanceAmount,
            balanceDueDate(weddingProfile, draft), false, null);
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

    private VendorStatus confirmedStatus(String action, DraftVendorCard draft) {
        if (isAddCandidate(action)) {
            return VendorStatus.CANDIDATE;
        }
        return draft.getStatus();
    }

    private boolean selectsAsCurrent(String action, VendorCard currentCard) {
        if (isChangeCurrent(action, currentCard)) {
            return true;
        }
        return !isAddCandidate(action) && currentCard == null;
    }

    private void unselectCurrent(String action, VendorCard currentCard) {
        if (!isChangeCurrent(action, currentCard)) {
            return;
        }
        currentCard.unselect();
    }

    private boolean isChangeCurrent(String action, VendorCard currentCard) {
        return "change".equals(action) && currentCard != null;
    }

    private boolean isAddCandidate(String action) {
        return "add-candidate".equals(action);
    }

    private LocalDate depositDueDate(DraftVendorCard draft) {
        if (draft.getContractDate() == null) {
            return LocalDate.now();
        }
        return draft.getContractDate();
    }

    private void saveSchedule(DraftVendorCard draft, VendorCard vendorCard) {
        if (draft.getScheduleDate() == null) {
            return;
        }
        LocalDateTime eventAt = LocalDateTime.of(draft.getScheduleDate(), scheduleTime(draft));
        vendorEventRepository.save(new VendorEvent(vendorCard, scheduleTitle(draft), eventAt, null));
    }

    private LocalTime scheduleTime(DraftVendorCard draft) {
        if (draft.getScheduleTime() == null) {
            return LocalTime.MIDNIGHT;
        }
        return draft.getScheduleTime();
    }

    private String scheduleTitle(DraftVendorCard draft) {
        if (draft.getScheduleTitle() == null || draft.getScheduleTitle().isBlank()) {
            return draft.getName() + " 일정";
        }
        return draft.getScheduleTitle().trim();
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

    private String confirmReply(TempCardConfirmRequest request, DraftVendorCard draft,
                                VendorCard currentCard, VendorCard vendorCard) {
        String categoryLabel = categoryLabel(draft.getCategory());
        if (isChangeCurrent(request.action(), currentCard)) {
            return changeReply(categoryLabel, currentCard, draft, request.reason());
        }
        if (isAddCandidate(request.action())) {
            return draft.getName() + "을(를) " + categoryLabel + " 후보로 추가했어요.";
        }
        if (vendorCard.isCurrent()) {
            return draft.getName() + "을(를) " + categoryLabel + " 업체로 등록했어요.";
        }
        return draft.getName() + "을(를) " + categoryLabel + " 후보 카드로 등록했어요.";
    }

    private void saveChangeHistory(
        WeddingProfile weddingProfile,
        DraftVendorCard draft,
        VendorCard currentCard,
        VendorCard vendorCard,
        TempCardConfirmRequest request
    ) {
        if (!isChangeCurrent(request.action(), currentCard)) {
            return;
        }
        vendorChangeHistoryRepository.save(new VendorChangeHistory(weddingProfile,
            draft.getCategory(), currentCard, vendorCard, request.reason()));
    }

    private String changeReply(
        String categoryLabel,
        VendorCard currentCard,
        DraftVendorCard draft,
        String reason
    ) {
        String reply = categoryLabel + "을 " + currentCard.getName()
            + "에서 " + draft.getName() + "(으)로 변경했어요.";
        if (reason != null && !reason.isBlank()) {
            return reply + " 변경 사유는 \"" + reason + "\"로 기록했어요.";
        }
        return reply;
    }

    private String categoryLabel(VendorCategory category) {
        return category.label();
    }

    private String eventTime(CardEventResponse event) {
        if (event.time() == null) {
            return "";
        }
        return event.time();
    }
}
