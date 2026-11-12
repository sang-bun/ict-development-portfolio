package umc.fitty.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.fitty.config.security.CustomUserDetailService;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        if (jwtUtil.validateToken(token)) {
            // 1. 토큰이 유효하면, 토큰에서 사용자 ID를 추출합니다.
            Long userId = jwtUtil.extractUserId(token);

            // 2. 사용자 ID를 기반으로 UserDetails 객체를 가져옵니다.
            UserDetails userDetails = customUserDetailService.loadUserByUsername(String.valueOf(userId));

            // 3. UserDetails를 기반으로 Authentication 객체를 생성합니다.
            //    이 객체는 Spring Security가 현재 사용자를 인식하는 데 사용됩니다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 4. SecurityContextHolder에 생성된 Authentication 객체를 설정합니다.
            //    이것이 바로 "현재 사용자를 인증 처리"하는 과정입니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}