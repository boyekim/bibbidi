package com.bibbidi.domain.vendor;

import com.bibbidi.domain.wedding.WeddingProfile;
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
public class WeddingTodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VendorCard vendorCard;

    @Column(nullable = false, length = 100)
    private String title;

    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean completed;

    @Lob
    private String memo;

    public WeddingTodo(WeddingProfile weddingProfile, VendorCard vendorCard, String title, LocalDate dueDate, String memo) {
        this.weddingProfile = weddingProfile;
        this.vendorCard = vendorCard;
        this.title = title;
        this.dueDate = dueDate;
        this.memo = memo;
    }

    public void complete() {
        this.completed = true;
    }

    public void reopen() {
        this.completed = false;
    }
}
