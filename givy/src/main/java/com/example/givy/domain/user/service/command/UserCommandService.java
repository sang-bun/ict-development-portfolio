package com.example.givy.domain.user.service.command;

import com.example.givy.domain.user.dto.req.UserReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import org.springframework.transaction.annotation.Transactional;

public interface UserCommandService {
    UserResDTO.UserInfoDTO signup(UserReqDTO.UserSignupDTO request);
    UserResDTO.UserLoginResDTO login(UserReqDTO.UserLoginDTO request);

    /* 01-04 소셜 회원가입(프로필 완성 단계) */
    void socialSignup(Long userId, UserReqDTO.UserProfileDTO dto);

    /* 01-06 로그아웃 API */
    void logout(Long userId, String refreshToken);

    /* 01-07 토큰 재발급 API */
    UserResDTO.UserTokenRefreshResDTO refresh(String rawRefreshToken);
}
