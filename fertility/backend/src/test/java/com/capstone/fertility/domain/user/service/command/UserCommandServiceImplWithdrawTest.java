package com.capstone.fertility.domain.user.service.command;

import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.enums.LoginType;
import com.capstone.fertility.domain.user.enums.UserStatus;
import com.capstone.fertility.domain.user.repository.UserRepository;
import com.capstone.fertility.global.auth.repository.RefreshTokenRepository;
import com.capstone.fertility.global.auth.service.RefreshTokenProvider;
import com.capstone.fertility.global.oauth.client.KakaoApiClient;
import com.capstone.fertility.global.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplWithdrawTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenProvider refreshTokenProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private KakaoApiClient kakaoApiClient;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    void withdraw_softDeletesActiveUser() {
        User user = User.builder()
                .id(10L)
                .email("fixture1@example.invalid")
                .kakaoId(999L)
                .loginType(LoginType.KAKAO)
                .nickname("tester")
                .isTermsAgreed(true)
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.findAllByPartner_Id(10L)).thenReturn(List.of());

        userCommandService.withdraw(10L);

        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user.getKakaoId()).isNull();
        assertThat(user.getEmail()).startsWith("withdrawn.10.");
        verify(kakaoApiClient).unlinkUser(999L);
        verify(refreshTokenRepository).deleteAllByUserId(10L);
        verify(userRepository, never()).delete(user);
    }

    @Test
    void withdraw_isIdempotentForAlreadyDeletedUser() {
        User user = User.builder()
                .id(11L)
                .email("fixture2@example.invalid")
                .loginType(LoginType.LOCAL)
                .nickname("gone")
                .status(UserStatus.DELETED)
                .isTermsAgreed(true)
                .build();

        when(userRepository.findById(11L)).thenReturn(Optional.of(user));

        userCommandService.withdraw(11L);

        verify(kakaoApiClient, never()).unlinkUser(anyLong());
        verify(refreshTokenRepository, never()).deleteAllByUserId(11L);
    }
}
