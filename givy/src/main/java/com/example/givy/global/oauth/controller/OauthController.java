package com.example.givy.global.oauth.controller;

/*
카카오 로그인 시작시키기 (카카오 로그인 페이지로 redirect)
카카오 인증서버가 돌려준 code 받고 JWT 발급해주기.
 */

import com.example.givy.domain.user.entity.Users;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.apiPayLoad.code.OauthSuccessCode;
import com.example.givy.global.auth.service.RefreshTokenProvider;
import com.example.givy.global.oauth.dto.req.OauthReqDTO;
import com.example.givy.global.oauth.model.GoogleUserInfo;
import com.example.givy.global.oauth.model.KakaoUserInfo;
import com.example.givy.global.oauth.service.GoogleOauthService;
import com.example.givy.global.oauth.service.KakaoOauthService;
import com.example.givy.global.oauth.service.OauthUserService;
import com.example.givy.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    private final KakaoOauthService kakaoOauthService;
    private final GoogleOauthService googleOauthService;
    private final OauthUserService oauthUserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    /**
     * 1) 카카오 로그인 시작
     *    클라이언트가 호출하면 → 카카오 로그인 페이지로 redirect됨
     */
    /* 01-03 카카오 로그인 API */
    @GetMapping("/kakao/login")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoOauthService.generateKakaoLoginUrl());
    }


    /**
     * 2) 카카오 인증 후 redirect_uri로 code가 넘어오는 콜백 URL
     *    예) GET /oauth/kakao/callback?code=xxxx
     */
    @GetMapping("/kakao/callback")
    public void kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) throws IOException {
        // 1. 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUser = kakaoOauthService.fetchKakaoUser(code);

        // 2. DB User 조회/생성
        Users user = oauthUserService.handleKakaoUser(kakaoUser);

        // 3. 토큰 발급
        String accessToken =
                jwtTokenProvider.createToken(user.getUserId(), user.getRole());

        String refreshToken =
                refreshTokenProvider.createAndSave(user.getUserId());

        // 4. 프론트엔드로 리다이렉트 (토큰을 URL 파라미터로 전달)
        response.sendRedirect("http://localhost:3000/?token=" + accessToken);
    }

    @GetMapping("/google/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleOauthService.generateGoogleLoginUrl());
    }

    @GetMapping("/google/callback")
    public void googleCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) throws IOException {
        GoogleUserInfo googleUser = googleOauthService.fetchGoogleUser(code);

        String jwt = oauthUserService.handleGoogleUser(googleUser);

        // 프론트엔드로 리다이렉트 (토큰을 URL 파라미터로 전달)
        response.sendRedirect("http://localhost:3000/?token=" + jwt);
    }

    /**
     * Google ID Token으로 로그인
     * 프론트엔드에서 Google One Tap 또는 GIS를 통해 받은 ID Token을 사용
     */
    @PostMapping("/google/id-token")
    public ApiResponse<String> googleIdTokenLogin(
            @RequestBody @Valid OauthReqDTO.GoogleIdTokenDTO request
    ) {
        // 1. ID Token 검증 및 사용자 정보 추출
        GoogleUserInfo googleUser = googleOauthService.fetchGoogleUserFromIdToken(request.getIdToken());

        // 2. DB User 조회/생성
        Users user = oauthUserService.handleGoogleUserAndReturnUser(googleUser);

        // 3. JWT 토큰 발급
        String accessToken = jwtTokenProvider.createToken(user.getUserId(), user.getRole());
        refreshTokenProvider.createAndSave(user.getUserId());

        return ApiResponse.onSuccess(OauthSuccessCode.GOOGLE_LOGIN_SUCCESS, accessToken);
    }
}
