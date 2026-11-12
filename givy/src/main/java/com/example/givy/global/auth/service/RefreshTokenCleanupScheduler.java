package com.example.givy.global.auth.service;

import com.example.givy.global.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 만료되었거나(revoked/expired) 사용 불가능한 RefreshToken 정리
     * - 매일 새벽 4시에 실행
     */
    @Scheduled(cron = "0 0 4 * * *") // 매일 04:00
    @Transactional
    public void cleanupRefreshTokens() {

        LocalDateTime now = LocalDateTime.now();

        refreshTokenRepository.deleteExpiredOrRevoked(now);

        log.info("[RefreshTokenCleanup] expired/revoked refresh tokens deleted at {}", now);
    }
}
