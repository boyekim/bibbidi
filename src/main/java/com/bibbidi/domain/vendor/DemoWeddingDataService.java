package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.wedding.WeddingProfile;
import com.bibbidi.domain.wedding.WeddingProfileRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DemoWeddingDataService {

    private final WeddingProfileRepository weddingProfileRepository;
    private final VendorCardRepository vendorCardRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final VendorEventRepository vendorEventRepository;
    private final VendorOptionRepository vendorOptionRepository;

    @Transactional
    public WeddingProfile ensureDefaultData(User user) {
        WeddingProfile weddingProfile = weddingProfileRepository.findFirstByOwnerOrderByIdAsc(user)
            .orElseGet(() -> weddingProfileRepository.save(new WeddingProfile(
                user,
                user.getName() + "의 결혼식",
                "지훈",
                LocalDate.of(2026, 10, 24),
                45_000_000L
            )));

        List<VendorCard> vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        if (vendorCards.isEmpty()) {
            vendorCardRepository.saveAll(defaultCards(weddingProfile));
            vendorCards = vendorCardRepository.findByWeddingProfileOrderByIdAsc(weddingProfile);
        }

        ensureDetails(vendorCards);

        return weddingProfile;
    }

    private void ensureDetails(List<VendorCard> vendorCards) {
        if (vendorCards.isEmpty()) {
            return;
        }

        Map<String, VendorCard> cardsByName = vendorCards.stream()
            .collect(Collectors.toMap(VendorCard::getName, Function.identity(), (first, second) -> first));

        if (paymentScheduleRepository.countByVendorCardIn(vendorCards) == 0) {
            paymentScheduleRepository.saveAll(defaultPayments(cardsByName));
        }

        if (vendorEventRepository.countByVendorCardIn(vendorCards) == 0) {
            vendorEventRepository.saveAll(defaultEvents(cardsByName));
        }

        if (vendorOptionRepository.countByVendorCardIn(vendorCards) == 0) {
            vendorOptionRepository.saveAll(defaultOptions(cardsByName));
        }
    }

    private List<VendorCard> defaultCards(WeddingProfile weddingProfile) {
        VendorCard weddingHall = new VendorCard(
            weddingProfile,
            VendorCategory.WEDDING_HALL,
            "라루체 컨벤션",
            VendorStatus.CONTRACTED,
            true
        );
        weddingHall.updateContract(
            LocalDate.of(2026, 6, 28),
            24_000_000L,
            3_000_000L,
            21_000_000L,
            LocalDate.of(2026, 9, 24)
        );
        weddingHall.updateMemo("토요일 오후 예식. 보증 인원 200명, 생화 장식 포함.");

        VendorCard studio = new VendorCard(
            weddingProfile,
            VendorCategory.STUDIO,
            "온더무드 스튜디오",
            VendorStatus.IN_PROGRESS,
            true
        );
        studio.updateContract(
            null,
            2_200_000L,
            300_000L,
            1_900_000L,
            LocalDate.of(2026, 8, 20)
        );
        studio.updateMemo("야외 컷 가능 여부 확인 필요. 원본 파일 포함 옵션 검토 중.");

        VendorCard dress = new VendorCard(
            weddingProfile,
            VendorCategory.DRESS,
            "아뜰리에 루미에르",
            VendorStatus.CANDIDATE,
            true
        );
        dress.updateContract(null, 2_100_000L, null, null, null);
        dress.updateMemo("2차 피팅 예약 완료. 실크 드레스 후보가 가장 마음에 듦.");

        VendorCard makeup = new VendorCard(
            weddingProfile,
            VendorCategory.MAKEUP,
            "블룸 메이크업",
            VendorStatus.SCHEDULED,
            true
        );
        makeup.updateContract(
            null,
            950_000L,
            100_000L,
            850_000L,
            LocalDate.of(2026, 10, 10)
        );
        makeup.updateMemo("혼주 메이크업 2인 추가 견적 확인 필요.");

        return List.of(weddingHall, studio, dress, makeup);
    }

    private List<PaymentSchedule> defaultPayments(Map<String, VendorCard> cardsByName) {
        return List.of(
            new PaymentSchedule(cardsByName.get("라루체 컨벤션"), "계약금", 3_000_000L, LocalDate.of(2026, 6, 28), true, "계약 당일 입금 완료"),
            new PaymentSchedule(cardsByName.get("라루체 컨벤션"), "잔금", 21_000_000L, LocalDate.of(2026, 9, 24), false, "예식 30일 전 입금"),
            new PaymentSchedule(cardsByName.get("온더무드 스튜디오"), "예약금", 300_000L, LocalDate.of(2026, 7, 15), false, "촬영일 확정 후 입금"),
            new PaymentSchedule(cardsByName.get("블룸 메이크업"), "예약금", 100_000L, LocalDate.of(2026, 7, 20), false, "계좌 문자 수신 대기")
        );
    }

    private List<VendorEvent> defaultEvents(Map<String, VendorCard> cardsByName) {
        return List.of(
            new VendorEvent(cardsByName.get("온더무드 스튜디오"), "스튜디오 상담", LocalDateTime.of(2026, 7, 18, 15, 0), "샘플 앨범과 야외 촬영 가능 여부 확인"),
            new VendorEvent(cardsByName.get("아뜰리에 루미에르"), "드레스 2차 피팅", LocalDateTime.of(2026, 7, 27, 11, 0), "베일과 볼레로 조합도 같이 보기"),
            new VendorEvent(cardsByName.get("블룸 메이크업"), "메이크업 리허설", LocalDateTime.of(2026, 8, 9, 13, 30), "헤어 변형 2회 가능 여부 문의")
        );
    }

    private List<VendorOption> defaultOptions(Map<String, VendorCard> cardsByName) {
        VendorOption dressOption = new VendorOption(
            cardsByName.get("아뜰리에 루미에르"),
            "실크 A라인",
            0L,
            "본식 후보. 베일 조합 확인 필요"
        );
        dressOption.select();

        VendorOption secondDressOption = new VendorOption(
            cardsByName.get("아뜰리에 루미에르"),
            "레이스 머메이드",
            180_000L,
            "라인은 예쁘지만 이동이 불편할 수 있음"
        );

        VendorOption makeupOption = new VendorOption(
            cardsByName.get("블룸 메이크업"),
            "혼주 메이크업 2인",
            300_000L,
            "양가 어머님 포함"
        );

        return List.of(dressOption, secondDressOption, makeupOption);
    }
}
