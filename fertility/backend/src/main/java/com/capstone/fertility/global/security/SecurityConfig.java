package com.capstone.fertility.global.security;

import com.capstone.fertility.domain.user.repository.UserRepository;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.apiPayLoad.code.GeneralErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
보안 규칙 + 필터 등록 + 인증 전략
1. 어떤 URL은 인증이 필요한지
2. 어떤 URL은 인증이 필요하지 않는지(회원가입/로그인)
3. 세션은 만들지 말고 JWT 기반으로 할 것
4. 우리가 만든 JwtAuthenticationFilter를 필터 체인에 등록
5. 비밀번호 암호화를 위해 PasswordEncoder 등록
6. CORS 설정
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Bean //이 메서드를 스프링이 실행해서 반환값을 Spring Bean으로 등록함.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //1. cors 적용
        http.cors(cors -> cors.configurationSource(CustomCorsConfigurationSource()));

        //2. csrf 비활성화
        http.csrf(csrf -> csrf.disable());

        //3. 서버에서 저장 안할거라 세션 사용 x
        http.sessionManagement(session ->
                session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        //4. URL 인가 규칙 설정
        http.authorizeHttpRequests(auth -> auth

                // 1. [인증 API 허용]
                // - /auth/** : 회원가입, 일반 로그인, 토큰 재발급 등 자체 인증 관련 API
                // - /oauth/** : 카카오, 구글 등 소셜 로그인 진행 및 콜백 API
                // 이 두 경로는 사용자가 아직 '토큰이 없는 상태'로 접근하므로 반드시 통과시켜야 합니다.
                .requestMatchers( "/oauth/**",
                        "/auth/signup",
                        "/auth/login",
                        "/auth/refresh").permitAll()

                // 2. [API 문서(Swagger) 허용]
                // 프론트엔드 개발자가 API 명세서를 보고 테스트할 수 있도록
                // Swagger UI 페이지와, 그 페이지를 그리는 데 필요한 내부 JSON 데이터(/v3/api-docs/**) 접근을 허용합니다.
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                // 3. git checkout develop[에러 페이지 허용]
                // 스프링 부트 내부에서 예외가 발생해 /error 경로로 포워딩될 때,
                // 이 경로마저 인증이 막혀있으면 진짜 에러 원인이 숨겨지고 '403 Forbidden'만 뜨는 것을 방지합니다.
                .requestMatchers("/error").permitAll()

                // 4. [기본 리소스 허용]
                // - / : 서버가 살았는지 죽었는지 확인하는 가장 기본(Root) 경로 (보통 헬스체크용)
                // - /index.html : API 서버라도 기본 환영 페이지가 있을 수 있음
                // - /favicon.ico : 웹 브라우저가 탭 아이콘을 그리기 위해 무조건 요청하는 파일 (안 열어두면 에러 로그가 더러워짐)
                .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()

                // 5. [그 외 모든 요청]
                // 위에서 명시적으로 허용(.permitAll())한 경로들을 제외한
                // 게시글 작성, 프로필 조회 등 "모든 나머지 API 요청"은 반드시 JWT 토큰 인증을 거쳐야만 합니다.
                .anyRequest().authenticated()
        );

        // 5. 인증 실패 시 예외 처리
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");

                    ApiResponse<?> body = ApiResponse.onFailure(GeneralErrorCode.UNAUTHORIZED);
                    String json = new ObjectMapper().writeValueAsString(body);
                    res.getWriter().write(json);
                })
        );

        // 6. 우리가 만든 JwtAuthenticationFilter 등록
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userRepository),
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }



    //Cross-Origin Resource Sharing - A 도메인에서 B 도메인으로 요청 보낼 때 발생하는 보안 정책
    //브라우저가 안전하게 [다른 origin]으로 요청할 수 있도록 허용 규칙을 세팅하는 역할.
    @Bean
    public CorsConfigurationSource CustomCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration(); //cors 규칙이 담긴 객체

        //url 허용 목록이 아니라 출처 허용 목록을 설정하는 곳임. (http://example.com 이런거)
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); //URL 패턴 /** 에 대해 config (CORS 규칙)을 등록한다는 의미.

        return source;

    }

}
