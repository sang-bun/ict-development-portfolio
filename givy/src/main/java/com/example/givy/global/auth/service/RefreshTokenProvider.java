package com.example.givy.global.auth.service;

import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.global.apiPayLoad.code.GeneralErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;
import com.example.givy.global.auth.entity.RefreshToken;
import com.example.givy.global.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 14;

    /** Refresh Token 생성 + DB 저장 */
    public String createAndSave(Long userId) {
        String rawToken = generateToken();
        String tokenHash = hash(rawToken);

        RefreshToken refreshToken = RefreshToken.create(
                userId,
                tokenHash,
                LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS)
        );

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    /** Refresh Token 검증 */
    public RefreshToken validate(String rawToken) {
        String tokenHash = hash(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_INVALID_REFRESH_TOKEN_OWNER));

        if (token.isExpired() || token.isRevoked()) {
            throw new GeneralException(GeneralErrorCode.EXPIRED_OR_REVOKED_REFRESH_TOKEN);
        }

        return token;
    }

    /** 로그아웃 */
    public void revoke(RefreshToken token) {
        token.revoke();
    }

    private String generateToken() { //랜덤 토큰 문자열 생성
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.TOKEN_HASH_FAILED);
        }
    }
}
