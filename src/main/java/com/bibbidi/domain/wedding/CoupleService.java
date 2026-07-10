package com.bibbidi.domain.wedding;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.DemoWeddingDataService;
import com.bibbidi.domain.vendor.PaymentSchedule;
import com.bibbidi.domain.vendor.PaymentScheduleRepository;
import com.bibbidi.domain.vendor.VendorCard;
import com.bibbidi.domain.vendor.VendorCardRepository;
import com.bibbidi.domain.wedding.dto.CoupleUpdateRequest;
import com.bibbidi.domain.wedding.dto.CoupleUpdateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final VendorCardRepository vendorCardRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;

    @Transactional
    public CoupleUpdateResponse update(User user, CoupleUpdateRequest request) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        user.updateName(request.nameA());
        weddingProfile.update(
            request.nameA() + "의 결혼식",
            request.nameB(),
            request.weddingDate(),
            request.totalBudget()
        );

        return CoupleUpdateResponse.from(weddingProfile, user, spent(weddingProfile));
    }

    private long spent(WeddingProfile weddingProfile) {
        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        return paymentScheduleRepository.findByVendorCardInOrderByDueDateAscIdAsc(vendorCards).stream()
            .filter(PaymentSchedule::isPaid)
            .map(PaymentSchedule::getAmount)
            .filter(amount -> amount != null)
            .mapToLong(Long::longValue)
            .sum();
    }
}
