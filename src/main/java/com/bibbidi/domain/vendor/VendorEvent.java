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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private VendorCard vendorCard;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime eventAt;

    @Lob
    private String memo;

    public VendorEvent(VendorCard vendorCard, String title, LocalDateTime eventAt, String memo) {
        this.vendorCard = vendorCard;
        this.title = title;
        this.eventAt = eventAt;
        this.memo = memo;
    }

    public void reschedule(LocalDateTime eventAt) {
        this.eventAt = eventAt;
    }
}
