package com.example.givy.domain.kis.service;

import com.example.givy.domain.kis.dto.KisTokenResponse;
import com.example.givy.domain.kis.entity.KisToken;
import com.example.givy.domain.kis.repository.KisTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisAuthService {

    @Value("${kis.api.app-key}") private String appKey;
    @Value("${kis.api.app-secret}") private String appSecret;
    @Value("${kis.api.url}") private String baseUrl;

    private final WebClient webClient;
    private final KisTokenRepository kisTokenRepository;
    private String accessToken; // 메모리 캐싱
    private LocalDateTime tokenExpirationTime; // 토큰 만료 시간

    // 서버 시작 시 및 6시간마다 토큰 갱신
    @Scheduled(fixedRate = 1000 * 60 * 60 * 6)
    @Transactional
    public void refreshAccessToken() {
        // 1. DB에서 토큰 불러오기 시도 (유효하면 API 호출 스킵)
        if (loadTokenFromDatabase()) {
            return;
        }

        log.info("KIS 액세스 토큰 갱신 시도 중...");

        String cleanKey = appKey.trim().replace("\"", "");
        String cleanSecret = appSecret.trim().replace("\"", "");

        if (cleanKey.startsWith("${") || cleanSecret.startsWith("${")) {
            log.error("KIS API Key가 설정되지 않았습니다.");
            return;
        }

        String requestUrl = baseUrl;
        if (requestUrl.endsWith("/oauth2/tokenP")) {
        } else {
            if (requestUrl.endsWith("/")) {
                requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
            }
            requestUrl += "/oauth2/tokenP";
        }

        KisTokenResponse response = webClient.post()
                .uri(requestUrl)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "grant_type", "client_credentials",
                        "appkey", cleanKey,
                        "appsecret", cleanSecret
                ))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class).doOnNext(body ->
                                log.error(" KIS 에러 상세 내용: {}", body)
                        ).then(Mono.error(new RuntimeException("KIS API 호출 실패")))
                )
                .bodyToMono(KisTokenResponse.class)
                .block();

        if (response != null && response.getAccessToken() != null) {
            this.accessToken = response.getAccessToken();
            // 만료 시간 설정 (여유 있게 10분 차감)
            this.tokenExpirationTime = LocalDateTime.now().plusSeconds(response.getExpiresIn() - 600);
            log.info("KIS 토큰 갱신 완료 (유효기간: {}초)", response.getExpiresIn());
            
            // DB에 저장
            saveTokenToDatabase();
        } else {
            log.error("KIS 토큰 발급 실패함");
        }
    }

    public String getStoredToken() {
        if (accessToken == null || (tokenExpirationTime != null && LocalDateTime.now().isAfter(tokenExpirationTime))) {
            refreshAccessToken();
        }
        return accessToken;
    }

    // --- DB 캐싱 로직 ---

    @Transactional
    private void saveTokenToDatabase() {
        try {
            // 기존 토큰 조회
            KisToken existingToken = kisTokenRepository.findFirstByOrderByCreatedAtDesc()
                    .orElse(null);

            if (existingToken != null) {
                // 기존 토큰 업데이트
                existingToken.updateToken(this.accessToken, this.tokenExpirationTime);
                kisTokenRepository.save(existingToken);
                log.info("KIS 토큰 DB 업데이트 완료 (만료: {})", this.tokenExpirationTime);
            } else {
                // 새 토큰 생성
                KisToken newToken = KisToken.create(this.accessToken, this.tokenExpirationTime);
                kisTokenRepository.save(newToken);
                log.info("KIS 토큰 DB 저장 완료 (만료: {})", this.tokenExpirationTime);
            }

            // 만료된 토큰 정리
            kisTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        } catch (Exception e) {
            log.warn("토큰 DB 저장 실패: {}", e.getMessage());
        }
    }

    private boolean loadTokenFromDatabase() {
        try {
            Optional<KisToken> tokenOpt = kisTokenRepository.findFirstByOrderByCreatedAtDesc();
            
            if (tokenOpt.isEmpty()) {
                log.info("DB에 저장된 토큰이 없습니다.");
                return false;
            }

            KisToken token = tokenOpt.get();

            // 토큰이 유효한지 확인
            if (!token.isExpired()) {
                this.accessToken = token.getAccessToken();
                this.tokenExpirationTime = token.getExpiredAt();
                log.info("DB에서 유효한 토큰 로드 완료 (만료: {})", token.getExpiredAt());
                return true; // 로드 성공
            } else {
                log.info("DB에 저장된 토큰이 만료되었습니다.");
                // 만료된 토큰 삭제
                kisTokenRepository.delete(token);
            }
        } catch (Exception e) {
            log.warn("토큰 DB 로드 실패 (새로 발급받습니다): {}", e.getMessage());
        }
        return false; // 로드 실패 또는 만료됨
    }
}
