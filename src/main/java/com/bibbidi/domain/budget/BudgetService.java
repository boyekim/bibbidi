package com.bibbidi.domain.budget;

import com.bibbidi.domain.budget.dto.BudgetItemResponse;
import com.bibbidi.domain.budget.dto.BudgetResponse;
import com.bibbidi.domain.overview.dto.OverviewCoupleResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.DemoWeddingDataService;
import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.PaymentScheduleRepository;
import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorCardRepository;
import com.bibbidi.domain.vendor.VendorStatus;
import com.bibbidi.domain.wedding.WeddingProfile;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BudgetService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final VendorCardRepository vendorCardRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;

    @Transactional
    public BudgetResponse getBudget(User user) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        List<PaymentSchedule> paymentSchedules = paymentScheduleRepository
            .findByVendorCardInOrderByDueDateAscIdAsc(vendorCards);
        long spent = paymentSchedules.stream()
            .filter(PaymentSchedule::isPaid)
            .map(PaymentSchedule::getAmount)
            .filter(amount -> amount != null)
            .mapToLong(Long::longValue)
            .sum();

        return new BudgetResponse(
            OverviewCoupleResponse.from(weddingProfile, user, spent),
            vendorCards.stream()
                .filter(card -> card.getStatus() != VendorStatus.IN_PROGRESS)
                .map(card -> BudgetItemResponse.from(card, paymentsFor(card, paymentSchedules)))
                .filter(item -> !item.payments().isEmpty())
                .toList()
        );
    }

    private List<PaymentSchedule> paymentsFor(VendorCard vendorCard, List<PaymentSchedule> paymentSchedules) {
        return paymentSchedules.stream()
            .filter(payment -> payment.getVendorCard().getId().equals(vendorCard.getId()))
            .toList();
    }
}
