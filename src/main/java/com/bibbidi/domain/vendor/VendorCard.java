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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorCategory category;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorStatus status;

    @Column(name = "current_selected", nullable = false)
    private boolean current;

    private LocalDate contractDate;

    private Long totalAmount;

    private Long depositAmount;

    private Long balanceAmount;

    private LocalDate balanceDueDate;

    @Lob
    private String memo;

    public VendorCard(
        WeddingProfile weddingProfile,
        VendorCategory category,
        String name,
        VendorStatus status,
        boolean current
    ) {
        this.weddingProfile = weddingProfile;
        this.category = category;
        this.name = name;
        this.status = status;
        this.current = current;
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

    public void changeStatus(VendorStatus status) {
        this.status = status;
    }

    public void selectAsCurrent() {
        this.current = true;
    }

    public void unselect() {
        this.current = false;
    }
}
