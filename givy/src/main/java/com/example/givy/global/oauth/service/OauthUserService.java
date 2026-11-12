package com.example.givy.global.oauth.service;
/*
“카카오 사용자 정보를 우리 DB User와 매핑하고 JWT 발급” 책임
소셜 종류(KAKAO, GOOGLE, NAVER 등)가 늘어나도
이 서비스는 공통 “소셜 사용자 처리” 로 사용할 수 있음.
 */

import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.enums.Language;
import com.example.givy.domain.user.enums.Role;
import com.example.givy.domain.user.enums.SocialType;
import com.example.givy.domain.user.repository.UserRepository;
import com.example.givy.global.oauth.model.GoogleUserInfo;
import com.example.givy.global.oauth.model.KakaoUserInfo;
import com.example.givy.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OauthUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Users handleKakaoUser(KakaoUserInfo info) {

        return userRepository.findByEmail(info.getEmail())
                .orElseGet(() -> createKakaoUser(info));
    }


    private Users createKakaoUser(KakaoUserInfo info){

        String encodedPassword = passwordEncoder.encode("kakao_oauth_placeholder");

        // 카카오 닉네임이 없으면 기본값 사용
        String nickname = info.getNickname() != null ? info.getNickname() : "카카오사용자";

        Users user = Users.builder()
                .email(info.getEmail())
                .password(encodedPassword)
                .socialType(SocialType.KAKAO)
                .name(nickname)  // name도 카카오 닉네임 사용
                .nickname(nickname)  // 카카오 프로필의 nickname 사용
                .role(Role.USER)
                .language(Language.KO)
                .connectedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public String handleGoogleUser(GoogleUserInfo info){
        Users user = userRepository.findByEmail(info.getEmail())
                .orElseGet(() -> createGoogleUser(info));

        return jwtTokenProvider.createToken(user.getUserId(), user.getRole());
    }

    // Users 객체를 반환하는 메서드 (ID Token 로그인용)
    public Users handleGoogleUserAndReturnUser(GoogleUserInfo info){
        return userRepository.findByEmail(info.getEmail())
                .orElseGet(() -> createGoogleUser(info));
    }

    private Users createGoogleUser(GoogleUserInfo info){
        String encodedPassword = passwordEncoder.encode("google_oauth_placeholder");

        Users user = Users.builder()
                .email(info.getEmail())
                .password(encodedPassword)
                .name("프로필을 완성해주세요")
                .nickname(info.getNickname())
                .role(Role.USER)
                .language(Language.KO)
                .build();

        return userRepository.save(user);
    }
}
