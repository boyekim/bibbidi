package com.bibbidi.domain.vendor;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private VendorCard vendorCard;

    @Column(nullable = false, length = 100)
    private String name;

    private Long extraCost;

    @Lob
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorOptionStatus status;

    public VendorOption(VendorCard vendorCard, String name, Long extraCost, String memo) {
        this.vendorCard = vendorCard;
        this.name = name;
        this.extraCost = extraCost;
        this.memo = memo;
        this.status = VendorOptionStatus.CANDIDATE;
    }

    public void select() {
        this.status = VendorOptionStatus.SELECTED;
    }

    public void exclude() {
        this.status = VendorOptionStatus.EXCLUDED;
    }
}
