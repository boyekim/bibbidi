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
public class WeddingInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @Column(nullable = false, length = 100)
    private String invitedEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WeddingMemberRole role;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WeddingInvitationStatus status;

    @Column(nullable = false)
    private LocalDateTime invitedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime acceptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User acceptedUser;

    public WeddingInvitation(
        WeddingProfile weddingProfile,
        String invitedEmail,
        WeddingMemberRole role,
        String token,
        LocalDateTime expiresAt
    ) {
        this.weddingProfile = weddingProfile;
        this.invitedEmail = invitedEmail;
        this.role = role;
        this.token = token;
        this.status = WeddingInvitationStatus.PENDING;
        this.invitedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public void accept(User acceptedUser) {
        this.acceptedUser = acceptedUser;
        this.status = WeddingInvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = WeddingInvitationStatus.EXPIRED;
    }

    public void cancel() {
        this.status = WeddingInvitationStatus.CANCELED;
    }
}
