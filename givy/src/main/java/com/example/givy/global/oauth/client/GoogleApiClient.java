package com.example.givy.global.oauth.client;

import com.example.givy.global.oauth.dto.res.GoogleIdTokenResponse;
import com.example.givy.global.oauth.dto.res.GoogleTokenResponse;
import com.example.givy.global.oauth.dto.res.GoogleUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class GoogleApiClient {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.google.token-uri}")
    private String tokenUri;

    @Value("${oauth.google.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;

    // 1) 인가 코드(code)로 토큰 요청
    public GoogleTokenResponse requestToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleTokenResponse> response =
                restTemplate.postForEntity(tokenUri, request, GoogleTokenResponse.class);

        return response.getBody();
    }

    // 2) access_token으로 사용자 정보 요청
    public GoogleUserResponse requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Authorization: Bearer {token}

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> response =
                restTemplate.exchange(userInfoUri, HttpMethod.GET, request, GoogleUserResponse.class);

        return response.getBody();
    }

    // 3) ID Token 검증 및 사용자 정보 추출
    public GoogleIdTokenResponse verifyIdToken(String idToken) {
        String tokenInfoUri = "https://oauth2.googleapis.com/tokeninfo";
        
        String url = UriComponentsBuilder
                .fromHttpUrl(tokenInfoUri)
                .queryParam("id_token", idToken)
                .toUriString();

        ResponseEntity<GoogleIdTokenResponse> response =
                restTemplate.getForEntity(url, GoogleIdTokenResponse.class);

        GoogleIdTokenResponse tokenInfo = response.getBody();
        
        // 클라이언트 ID 검증
        if (tokenInfo != null && !clientId.equals(tokenInfo.getAud())) {
            throw new RuntimeException("Invalid ID token: client ID mismatch");
        }

        return tokenInfo;
    }
}