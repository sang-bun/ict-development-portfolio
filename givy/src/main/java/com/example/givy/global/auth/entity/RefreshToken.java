package com.example.givy.global.auth.entity;

import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long refreshTokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    public static RefreshToken create(
            Long userId,
            String tokenHash,
            LocalDateTime expiredAt
    ) {
        return new RefreshToken(
                null,
                userId,
                tokenHash,
                expiredAt,
                null
        );
    }

    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    /*

    refresh 요청
    → 기존 refresh token 사용
    → 새 refresh token 발급
    → 기존 refresh token revoke

    */

}
