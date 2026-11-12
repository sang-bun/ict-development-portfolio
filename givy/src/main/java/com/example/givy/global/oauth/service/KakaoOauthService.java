package com.example.givy.global.oauth.service;

/*
code → access_token 요청
access_token → 사용자 정보 요청
== code → token → userInfo → 내부 모델 변환 → OauthUserService로 넘김
 */

import com.example.givy.global.oauth.client.KakaoApiClient;
import com.example.givy.global.oauth.dto.res.KakaoTokenResponse;
import com.example.givy.global.oauth.dto.res.KakaoUserResponse;
import com.example.givy.global.oauth.model.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {

    private final KakaoApiClient kakaoApiClient;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.kakao.authorize-uri}")
    private String authorizeUri;


    public String generateKakaoLoginUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(authorizeUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "account_email,profile_nickname")  // 이메일과 프로필 정보 요청
                .build()
                .toUriString();
    }

    public KakaoUserInfo fetchKakaoUser(String code) {

        // (1) code → access_token
        KakaoTokenResponse token = kakaoApiClient.requestToken(code);

        // (2) access_token → 사용자 정보
        KakaoUserResponse userResponse =
                kakaoApiClient.requestUserInfo(token.getAccess_token());

        // (3) KakaoUserResponse DTO → 우리 내부 공통 모델 변환
        // null 체크 추가: 사용자가 이메일 제공 동의를 하지 않았거나 프로필 정보가 없을 수 있음
        String email = null;
        String nickname = null;
        
        if (userResponse.getKakao_account() != null) {
            email = userResponse.getKakao_account().getEmail();
            if (userResponse.getKakao_account().getProfile() != null) {
                nickname = userResponse.getKakao_account().getProfile().getNickname();
            }
        }
        
        return KakaoUserInfo.builder()
                .kakaoId(userResponse.getId())
                .email(email)
                .nickname(nickname != null ? nickname : "카카오사용자")  // 닉네임이 없으면 기본값 사용
                .build();
    }
}
