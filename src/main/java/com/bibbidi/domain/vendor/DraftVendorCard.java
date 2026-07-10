package com.bibbidi.domain.vendor;

import com.bibbidi.domain.wedding.WeddingProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DraftVendorCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private VendorCategory category;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorStatus status;

    private LocalDate contractDate;

    private Long totalAmount;

    private Long depositAmount;

    private Long balanceAmount;

    private LocalDate balanceDueDate;

    @Lob
    private String memo;

    @Lob
    private String sourceMessage;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;

    @Column(length = 100)
    private String scheduleTitle;

    public DraftVendorCard(WeddingProfile weddingProfile, VendorStatus status, String sourceMessage) {
        this.weddingProfile = weddingProfile;
        this.status = status;
        this.sourceMessage = sourceMessage;
    }

    public void updateVendorInfo(VendorCategory category, String name, VendorStatus status) {
        this.category = category;
        this.name = name;
        this.status = status;
    }

    public void updateContract(
        LocalDate contractDate,
        Long totalAmount,
        Long depositAmount,
        Long balanceAmount,
        LocalDate balanceDueDate
    ) {
        this.contractDate = contractDate;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;
        this.balanceAmount = balanceAmount;
        this.balanceDueDate = balanceDueDate;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateSourceMessage(String sourceMessage) {
        this.sourceMessage = sourceMessage;
    }

    public void updateSchedule(LocalDate scheduleDate, LocalTime scheduleTime, String scheduleTitle) {
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
        this.scheduleTitle = scheduleTitle;
    }
}
