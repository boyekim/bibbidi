package com.bibbidi.domain.vendor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class PaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private VendorCard vendorCard;

    @Column(nullable = false, length = 50)
    private String label;

    private Long amount;

    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean paid;

    @Lob
    private String memo;

    public PaymentSchedule(
        VendorCard vendorCard,
        String label,
        Long amount,
        LocalDate dueDate,
        boolean paid,
        String memo
    ) {
        this.vendorCard = vendorCard;
        this.label = label;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paid = paid;
        this.memo = memo;
    }

    public void complete() {
        this.paid = true;
    }

    public void reopen() {
        this.paid = false;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
