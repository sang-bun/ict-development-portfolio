package com.example.givy.global.oauth.service;

import com.example.givy.global.oauth.client.GoogleApiClient;
import com.example.givy.global.oauth.dto.res.GoogleTokenResponse;
import com.example.givy.global.oauth.dto.res.GoogleUserResponse;
import com.example.givy.global.oauth.model.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GoogleOauthService {

    private final GoogleApiClient googleApiClient;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    // application.yml에 oauth.google.authorize-uri를 추가해야 합니다.
    // (https://accounts.google.com/o/oauth2/v2/auth)
    @Value("${oauth.google.authorize-uri}")
    private String authorizeUri;

    // 프론트엔드용 로그인 URL 생성 (필요 시 사용)
    public String generateGoogleLoginUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(authorizeUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "email profile") // 구글은 scope 필수
                .build()
                .toUriString();
    }

    // 로그인 로직의 핵심
    public GoogleUserInfo fetchGoogleUser(String code) {
        // (1) code → access_token
        GoogleTokenResponse token = googleApiClient.requestToken(code);

        // (2) access_token → 사용자 정보
        GoogleUserResponse userResponse = googleApiClient.requestUserInfo(token.getAccess_token());

        // (3) GoogleUserResponse DTO → 우리 내부 공통 모델 변환
        return GoogleUserInfo.builder()
                .googleId(userResponse.getSub())
                .email(userResponse.getEmail())
                .nickname(userResponse.getName())
                .profileImage(userResponse.getPicture())
                .build();
    }

    // ID Token으로 사용자 정보 추출
    public GoogleUserInfo fetchGoogleUserFromIdToken(String idToken) {
        // (1) ID Token 검증 및 사용자 정보 추출
        var tokenInfo = googleApiClient.verifyIdToken(idToken);

        // (2) GoogleIdTokenResponse → 우리 내부 공통 모델 변환
        return GoogleUserInfo.builder()
                .googleId(tokenInfo.getSub())
                .email(tokenInfo.getEmail())
                .nickname(tokenInfo.getName())
                .profileImage(tokenInfo.getPicture())
                .build();
    }
}