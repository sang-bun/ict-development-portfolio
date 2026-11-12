package com.capstone.fertility.global.security;

import com.capstone.fertility.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomPrincipal {
    private Long userId;
    private Role role;
}

/*
스프링의 SecurityContext는 Authentication 객체 안에 principal(주체) 정보를 넣음.
이 principal 안에는
1. 누가 로그인했는지
2. 어떤 role인지
3. userId가 뭔지 들어있음.
CustomPrincipal = “JWT로 인증된 사용자 정보를 담아두는 스프링용 유저 객체”


1. SecurityContextHolder에 저장됨
2. JwtAuthenticationFilter에서 Authentication 객체를 만들 때 principal로 들어갈 값이고
3. Controller에서 [@AuthenticationPrincipal CustomPrincipal principal] 로 바로 꺼낼 수 있음
 */