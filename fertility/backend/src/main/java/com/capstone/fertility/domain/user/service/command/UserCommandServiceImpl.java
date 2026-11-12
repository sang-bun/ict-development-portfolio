package com.capstone.fertility.domain.user.service.command;

import com.capstone.fertility.domain.user.converter.UserConverter;
import com.capstone.fertility.domain.user.dto.req.UserReqDTO;
import com.capstone.fertility.domain.user.dto.res.UserResDTO;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.domain.user.enums.LoginType;
import com.capstone.fertility.domain.user.enums.Role;
import com.capstone.fertility.domain.user.enums.UserStatus;
import com.capstone.fertility.global.auth.repository.RefreshTokenRepository;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import com.capstone.fertility.global.auth.service.RefreshTokenProvider;
import com.capstone.fertility.global.oauth.client.KakaoApiClient;
import com.capstone.fertility.global.security.JwtTokenProvider;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoApiClient kakaoApiClient;

    @Override
    public UserResDTO.LoginResDTO signUp(UserReqDTO.SignUpReqDTO request) {
        if (userRepository.existsByEmailAndStatus(request.email(), UserStatus.ACTIVE)) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = UserConverter.toLocalUser(
                request.email(),
                encodedPassword,
                request.nickname(),
                Boolean.TRUE.equals(request.isTermsAgreed())
        );
        User savedUser = userRepository.save(newUser);

        String accessToken = jwtTokenProvider.createToken(savedUser.getId(), Role.USER);
        String refreshToken = refreshTokenProvider.createAndSave(savedUser.getId());

        return UserConverter.toLoginResDTO(accessToken, refreshToken, savedUser);
    }

    @Override
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        if (!user.isActive()) {
            return;
        }

        Long kakaoId = user.getKakaoId();
        if (kakaoId != null) {
            try {
                kakaoApiClient.unlinkUser(kakaoId);
            } catch (Exception e) {
                System.err.println("카카오 언링크 실패: " + e.getMessage());
            }
        }

        userRepository.findAllByPartner_Id(userId).forEach(User::clearPartner);
        user.withdraw();
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Override
    public UserResDTO.UserInfoDTO updateMyInfo(Long userId, UserReqDTO.UpdateProfileDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));
        ensureActive(user);

        if (request.getDisplayName() != null) {
            String trimmed = request.getDisplayName().trim();
            if (!trimmed.isEmpty() && trimmed.length() > 20) {
                throw new UserException(UserErrorCode.INVALID_DISPLAY_NAME);
            }
        }

        Gender parsedGender = null;
        if (request.getGender() != null && !request.getGender().isBlank()) {
            try {
                parsedGender = Gender.from(request.getGender());
            } catch (IllegalArgumentException e) {
                throw new UserException(UserErrorCode.INVALID_GENDER);
            }
        }

        user.patchProfile(
                request.getNickname(),
                request.getProfileImageUrl(),
                request.getDisplayName(),
                parsedGender
        );

        return UserConverter.toUserInfoDTO(user);
    }

    @Override
    public UserResDTO.LoginResDTO login(UserReqDTO.LoginReqDTO request) {

        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new UserException(UserErrorCode.USER_EMAIL_NOT_FOUND));
        ensureActive(user);
        ensureEmailLoginAllowed(user);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), Role.USER);
        String refreshToken = refreshTokenProvider.createAndSave(user.getId());

        return UserConverter.toLoginResDTO(accessToken, refreshToken, user);
    }

    private void ensureEmailLoginAllowed(User user) {
        LoginType loginType = user.getLoginType();
        if (loginType == LoginType.KAKAO || (loginType == null && user.getKakaoId() != null)) {
            throw new UserException(UserErrorCode.EMAIL_LOGIN_NOT_SUPPORTED_FOR_SOCIAL);
        }
    }

    private void ensureActive(User user) {
        if (!user.isActive()) {
            throw new UserException(UserErrorCode.USER_WITHDRAWN);
        }
    }
}