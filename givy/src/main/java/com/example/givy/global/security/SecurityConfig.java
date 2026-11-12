package com.example.givy.global.security;


import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.apiPayLoad.code.GeneralErrorCode;
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
                        // 회원가입 / 로그인 허용
                        .requestMatchers("/auth/signup", "/auth/login","/auth/refresh").permitAll()

                        // 정적 파일 허용
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**").permitAll()

                        // API 문서(Swagger) 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // OAuth Redirect 허용
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/oauth/google/**").permitAll()  // Google OAuth 명시적 허용
                        
                        // 테스트용 KIS 스케줄러 수동 실행 (개발 환경용)
                        .requestMatchers("/test/kis/**").permitAll()
                        
                        // Actuator Health Check 허용 (헬스체크용)
                        .requestMatchers("/actuator/health").permitAll()
                        
                        // 그 외 모든 요청은 인증 필요
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
                new JwtAuthenticationFilter(jwtTokenProvider),
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
