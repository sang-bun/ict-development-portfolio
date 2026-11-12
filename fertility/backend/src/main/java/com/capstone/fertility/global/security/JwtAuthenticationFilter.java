package com.capstone.fertility.global.security;

import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.enums.Role;
import com.capstone.fertility.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
1. 헤더에서 Authorization 읽기
2. Bearer xxx 인지 확인
3. 토큰 유효성 검증
4. userId, role 꺼내기
5. CustomPrincipal + Authentication 생성
6. SecurityContextHolder에 set
7. 다음 필터로 넘기기
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 필터는 SecurityConfig에서 new로 직접 만들어 쓸 거라 @Component 필요 X
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 0. OAuth 로그인 과정은 JWT 검사 대상에서 제외
        if (path.startsWith("/oauth/")) {
            filterChain.doFilter(request, response);
            return;
        }


        //1. 요청에서 Authorization 헤더 추출
        String token = resolveToken(request);

        // 2. 토큰이 없으면 -> 그냥 다음 필터로 넘김 (인증 없음 상태)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //3. 토큰이 있을 경우 유효성 검사
        if(!jwtTokenProvider.validateToken(token)) {
            // 유효하지 않으면 인증 세팅 안 하고 그대로 다음 필터
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtTokenProvider.getUserId(token);
        Role role = jwtTokenProvider.getRole(token);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.isActive()) {
            filterChain.doFilter(request, response);
            return;
        }

        CustomPrincipal principal = new CustomPrincipal(userId, role);

        //5. 권한 생성
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

        //6. UsernamePasswordAuthenticationToken 만들기
        //스프링 시큐리티는 인증된 사용자를 Authentication 객체로 표현함.
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,          // principal (누구냐?)
                        null,               // credentials (비밀번호는 필요 없음)
                        List.of(authority)  // 권한 목록
                );
        //7. SecurityContextHolder는 현재 요청의 보안 정보(인증/인가)를 저장하는 전역적인 저장소임. 여기에 저장하기.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //8. 다음 필터 넘기기 (filter -> DispatcherServlet -> Controller)
        filterChain.doFilter(request, response);


    }


    /**
     * Authorization 헤더에서 Bearer 토큰만 깔끔하게 추출
     * ex) "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 이후 실제 토큰 부분만 잘라냄
        }

        return null;
    }

}
