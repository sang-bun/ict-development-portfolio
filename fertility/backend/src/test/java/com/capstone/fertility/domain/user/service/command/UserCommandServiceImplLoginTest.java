package com.capstone.fertility.domain.user.service.command;

import com.capstone.fertility.domain.user.dto.req.UserReqDTO;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.enums.LoginType;
import com.capstone.fertility.domain.user.enums.Role;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import com.capstone.fertility.global.auth.service.RefreshTokenProvider;
import com.capstone.fertility.global.oauth.client.KakaoApiClient;
import com.capstone.fertility.global.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplLoginTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenProvider refreshTokenProvider;
    @Mock
    private KakaoApiClient kakaoApiClient;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    void login_succeeds_forLocalUser() {
        User user = User.builder()
                .id(1L)
                .email("fixture1@example.invalid")
                .password("encoded")
                .nickname("tester")
                .loginType(LoginType.LOCAL)
                .isTermsAgreed(true)
                .build();

        when(userRepository.findByEmail("fixture1@example.invalid")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtTokenProvider.createToken(1L, Role.USER)).thenReturn("access");
        when(refreshTokenProvider.createAndSave(1L)).thenReturn("refresh");

        var result = userCommandService.login(new UserReqDTO.LoginReqDTO("fixture1@example.invalid", "password123"));

        assertThat(result.accessToken()).isEqualTo("access");
        assertThat(result.refreshToken()).isEqualTo("refresh");
        verify(refreshTokenProvider).createAndSave(1L);
    }

    @Test
    void login_rejectsKakaoAccount() {
        User user = User.builder()
                .id(2L)
                .email("fixture2@example.invalid")
                .loginType(LoginType.KAKAO)
                .nickname("kakao")
                .isTermsAgreed(true)
                .build();

        when(userRepository.findByEmail("fixture2@example.invalid")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userCommandService.login(new UserReqDTO.LoginReqDTO("fixture2@example.invalid", "password123")))
                .isInstanceOf(UserException.class)
                .extracting(ex -> ((UserException) ex).getCode())
                .isEqualTo(UserErrorCode.EMAIL_LOGIN_NOT_SUPPORTED_FOR_SOCIAL);
    }

    @Test
    void login_treatsMissingLoginTypeAsLocalWhenNoKakaoId() {
        User user = User.builder()
                .id(3L)
                .email("fixture3@example.invalid")
                .password("encoded")
                .nickname("legacy")
                .isTermsAgreed(true)
                .build();

        when(userRepository.findByEmail("fixture3@example.invalid")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtTokenProvider.createToken(eq(3L), any())).thenReturn("access");
        when(refreshTokenProvider.createAndSave(3L)).thenReturn("refresh");

        var result = userCommandService.login(new UserReqDTO.LoginReqDTO("fixture3@example.invalid", "password123"));

        assertThat(result.accessToken()).isEqualTo("access");
    }
}
