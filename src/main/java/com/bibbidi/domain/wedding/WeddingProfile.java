package com.bibbidi.domain.wedding;

import com.bibbidi.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeddingProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User owner;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 30)
    private String partnerName;

    private LocalDate weddingDate;

    private Long totalBudget;

    public WeddingProfile(User owner, String name, String partnerName, LocalDate weddingDate, Long totalBudget) {
        this.owner = owner;
        this.name = name;
        this.partnerName = partnerName;
        this.weddingDate = weddingDate;
        this.totalBudget = totalBudget;
    }

    public void update(String name, String partnerName, LocalDate weddingDate, Long totalBudget) {
        this.name = name;
        this.partnerName = partnerName;
        this.weddingDate = weddingDate;
        this.totalBudget = totalBudget;
    }
}
