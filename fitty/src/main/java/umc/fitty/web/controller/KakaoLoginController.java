package umc.fitty.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.fitty.apiPayload.ApiResponse;
import umc.fitty.config.security.jwt.JwtToken;
import umc.fitty.service.KakaoService;

@Slf4j
@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<JwtToken>> callback(@RequestParam("code") String code) {
        JwtToken tokens = kakaoService.loginWithKakao(code);
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인 성공", tokens));
    }
}
