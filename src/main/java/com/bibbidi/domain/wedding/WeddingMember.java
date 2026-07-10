package com.bibbidi.domain.wedding;

import com.bibbidi.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeddingMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WeddingMemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WeddingMemberStatus status;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    public WeddingMember(WeddingProfile weddingProfile, User user, WeddingMemberRole role) {
        this.weddingProfile = weddingProfile;
        this.user = user;
        this.role = role;
        this.status = WeddingMemberStatus.ACTIVE;
        this.joinedAt = LocalDateTime.now();
    }

    public void changeRole(WeddingMemberRole role) {
        this.role = role;
    }

    public void remove() {
        this.status = WeddingMemberStatus.REMOVED;
    }
}
