package umc.fitty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import umc.fitty.config.security.jwt.JwtToken;
import umc.fitty.config.security.jwt.JwtUtil;
import umc.fitty.domain.User;
import umc.fitty.web.dto.KakaoLoginDTO.KakaoTokenResponseDto;
import umc.fitty.web.dto.KakaoLoginDTO.KakaoUserInfoResponseDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final WebClient webClient;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /** 인가코드로 액세스 토큰 교환 */
    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDto token = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new RuntimeException("Kakao token 4xx: " + b))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new RuntimeException("Kakao token 5xx: " + b))))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        if (token == null || token.getAccessToken() == null) {
            throw new RuntimeException("Failed to get Kakao access token");
        }
        log.info("[Kakao] access_token issued");
        return token.getAccessToken();
    }

    /** 액세스 토큰으로 사용자 정보 조회 */
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto info = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new RuntimeException("Kakao userinfo 4xx: " + b))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new RuntimeException("Kakao userinfo 5xx: " + b))))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        if (info == null) throw new RuntimeException("Failed to fetch Kakao user info");
        return info;
    }

    /** 전체 로그인 플로우 */
    public JwtToken loginWithKakao(String code) {
        String kakaoAccessToken = getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto info = getUserInfo(kakaoAccessToken);

        String kakaoId   = String.valueOf(info.getId());
        String email = (info.getKakaoAccount() != null) ? info.getKakaoAccount().getEmail() : null;
        String nickname  = (info.getKakaoAccount() != null && info.getKakaoAccount().getProfile() != null)
                ? info.getKakaoAccount().getProfile().getNickName() : null;
        String profile   = (info.getKakaoAccount() != null && info.getKakaoAccount().getProfile() != null)
                ? info.getKakaoAccount().getProfile().getProfileImageUrl() : null;

        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Kakao email is null/blank. Check 'account_email' scope & consent.");
        }

        User user = userService.upsertFromKakao(kakaoId, email, nickname, profile);
        String access = jwtUtil.createAccessToken(user.getId());

        return new JwtToken(access);
    }
}
