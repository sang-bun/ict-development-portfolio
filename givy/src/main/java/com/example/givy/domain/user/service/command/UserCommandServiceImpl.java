package com.example.givy.domain.user.service.command;

import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.converter.UserConverter;
import com.example.givy.domain.user.dto.req.UserReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import com.example.givy.global.auth.entity.RefreshToken;
import com.example.givy.global.auth.service.RefreshTokenProvider;
import com.example.givy.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    /* 01-01 회원가입 API */
    @Override
    public UserResDTO.UserInfoDTO signup(UserReqDTO.UserSignupDTO request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserException(UserErrorCode.USER_DUPLICATED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Users user = UserConverter.toEntity(request, encodedPassword);

        Users savedUser = userRepository.save(user);

        return UserConverter.toDTO(savedUser);

    }

    /* 01-02 로그인 API */
    @Override
    public UserResDTO.UserLoginResDTO login(UserReqDTO.UserLoginDTO request) {
        Users user = userRepository.findByEmail(request.getEmail()) //null일 수도 있는 값을 감싸는 래퍼 타입 - Optional
                .orElseThrow(() -> new UserException(UserErrorCode.USER_EMAIL_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new UserException(UserErrorCode.USER_INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(user.getUserId(), user.getRole());

        String refreshToken = refreshTokenProvider.createAndSave(user.getUserId());

        return UserConverter.toLoginDTO(accessToken, refreshToken, user);
    }

    /* 01-04 소셜 회원가입(프로필 완성 단계) */
    @Override
    public void socialSignup(Long userId, UserReqDTO.UserProfileDTO dto){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        //OauthUserService에서 카카오로 로그인한 유저 미리 하드코딩으로 NOTNULL 데이터들 넣어줬음. 다시 덮어씌우기.
        //이미 생성된 entity를 userId기준으로 찾았음.
        user.completeSocialProfile(dto);

        userRepository.save(user);
    }

    /* 01-06 로그아웃 API */
    @Override
    public void logout(Long userId, String refreshToken) {

        // 1refresh token 자체 검증 (존재 + 만료/폐기 여부)
        RefreshToken token = refreshTokenProvider.validate(refreshToken);

        if (!token.getUserId().equals(userId)) {
            throw new UserException(UserErrorCode.USER_INVALID_REFRESH_TOKEN_OWNER);
        }

        // 해당 유저 refreshToken 로그아웃시키기
        refreshTokenProvider.revoke(token);
    }

    /* 01-07 토큰 재발급 API */
    @Override
    public UserResDTO.UserTokenRefreshResDTO refresh(String rawRefreshToken) {

        RefreshToken token = refreshTokenProvider.validate(rawRefreshToken);

        Long userId = token.getUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createToken(user.getUserId(), user.getRole());

        String newRefreshToken = refreshTokenProvider.createAndSave(userId);

        //기존 refresh token 폐기. - 추후 RefreshTokenProvider.validate() 여기서 만료 검증됨.
        refreshTokenProvider.revoke(token);

        return UserResDTO.UserTokenRefreshResDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


}
