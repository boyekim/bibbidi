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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemoAnalysisSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private VendorCard vendorCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemoSuggestionKind kind;

    @Lob
    @Column(nullable = false)
    private String payloadJson;

    @Lob
    private String evidenceText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemoSuggestionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    public MemoAnalysisSuggestion(VendorCard vendorCard, MemoSuggestionKind kind, String payloadJson, String evidenceText) {
        this.vendorCard = vendorCard;
        this.kind = kind;
        this.payloadJson = payloadJson;
        this.evidenceText = evidenceText;
        this.status = MemoSuggestionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void apply() {
        this.status = MemoSuggestionStatus.APPLIED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void ignore() {
        this.status = MemoSuggestionStatus.IGNORED;
        this.resolvedAt = LocalDateTime.now();
    }
}
