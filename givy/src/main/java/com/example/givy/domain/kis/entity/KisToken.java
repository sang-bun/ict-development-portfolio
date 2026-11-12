package com.example.givy.domain.kis.entity;

import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "kis_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KisToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kis_token_id")
    private Long kisTokenId;

    @Column(name = "access_token", nullable = false, unique = true, length = 500)
    private String accessToken;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public static KisToken create(String accessToken, LocalDateTime expiredAt) {
        return new KisToken(
                null,
                accessToken,
                expiredAt
        );
    }

    public void updateToken(String accessToken, LocalDateTime expiredAt) {
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}
