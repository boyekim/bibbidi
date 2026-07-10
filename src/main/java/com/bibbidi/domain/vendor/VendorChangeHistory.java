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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VendorCard previousCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VendorCard newCard;

    @Column(nullable = false, length = 100)
    private String previousVendorName;

    @Column(nullable = false, length = 100)
    private String newVendorName;

    @Lob
    private String reason;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public VendorChangeHistory(
        WeddingProfile weddingProfile,
        VendorCategory category,
        VendorCard previousCard,
        VendorCard newCard,
        String reason
    ) {
        this.weddingProfile = weddingProfile;
        this.category = category;
        this.previousCard = previousCard;
        this.newCard = newCard;
        this.previousVendorName = previousCard.getName();
        this.newVendorName = newCard.getName();
        this.reason = reason;
        this.changedAt = LocalDateTime.now();
    }
}
