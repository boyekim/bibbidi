package com.bibbidi.domain.chat;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WeddingProfile weddingProfile;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 30)
    private String kind;

    @Lob
    @Column(nullable = false)
    private String payloadJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ChatItem(WeddingProfile weddingProfile, String role, String kind, String payloadJson) {
        this.weddingProfile = weddingProfile;
        this.role = role;
        this.kind = kind;
        this.payloadJson = payloadJson;
        this.createdAt = LocalDateTime.now();
    }
}
