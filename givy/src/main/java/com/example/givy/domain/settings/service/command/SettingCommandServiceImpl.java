package com.example.givy.domain.settings.service.command;

import com.example.givy.domain.notification.entity.UserNotificationSetting;
import com.example.givy.domain.notification.enums.NotificationType;
import com.example.givy.domain.notification.repository.UserNotificationSettingRepository;
import com.example.givy.domain.recommendation.entity.Tendency;
import com.example.givy.domain.recommendation.repository.RecommendationEventRepository;
import com.example.givy.domain.recommendation.repository.TendencyRepository;
import com.example.givy.domain.settings.code.SettingErrorCode;
import com.example.givy.domain.settings.dto.req.SettingReqDTO;
import com.example.givy.domain.settings.exception.SettingException;
import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingCommandServiceImpl implements SettingCommandService {

    private final UserRepository userRepository;
    private final TendencyRepository tendencyRepository;
    private final RecommendationEventRepository recommendationEventRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final PasswordEncoder passwordEncoder;

    /* 05-01 프로필 수정 */
    @Override
    public void updateProfile(Long userId, SettingReqDTO.UpdateProfileDTO request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        user.updateProfile(
                request.getNickname(),
                request.getLanguage(),
                request.getProfileImageUrl()
        );
    }

    /* 05-02 비밀번호 변경 */
    @Override
    public void updatePassword(Long userId, SettingReqDTO.UpdatePasswordDTO request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        // 소셜 로그인 유저는 비밀번호 변경 불가
        if (user.getSocialType() != null) {
            throw new SettingException(SettingErrorCode.PASSWORD_NOT_ALLOWED_FOR_SOCIAL_USER);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new SettingException(SettingErrorCode.INVALID_CURRENT_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /* 05-03 투자 성향 재설정 */
    @Override
    public void resetTendency(Long userId) {

        Tendency tendency = tendencyRepository
                .findTopByUsers_UserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.TENDENCY_NOT_FOUND));

        // 외래 키 제약 조건을 해결하기 위해 관련 RecommendationEvent 먼저 삭제
        recommendationEventRepository.deleteAllByTendency(tendency);

        tendencyRepository.delete(tendency);
    }

    /* 05-05 알림 수신 설정 변경 */
    @Override
    public void updateNotificationSetting(
            Long userId,
            SettingReqDTO.UpdateNotificationDTO request
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        updateSingleNotificationSetting(
                user,
                NotificationType.GUIDE,
                request.getGuideNotificationEnabled()
        );

        updateSingleNotificationSetting(
                user,
                NotificationType.CHALLENGE,
                request.getChallengeNotificationEnabled()
        );
    }

    private void updateSingleNotificationSetting(
            Users user,
            NotificationType notificationType,
            Boolean enabled
    ) {
        UserNotificationSetting setting = userNotificationSettingRepository
                .findByUsers_UserIdAndNotificationType(
                        user.getUserId(),
                        notificationType
                )
                .orElseGet(() ->
                        UserNotificationSetting.builder()
                                .users(user)
                                .notificationType(notificationType)
                                .enabled(enabled)
                                .build()
                );

        setting.updateEnabled(enabled);
        userNotificationSettingRepository.save(setting);
    }

    /* 05-06 앱 언어 변경 */
    @Override
    public void updateLanguage(Long userId, SettingReqDTO.UpdateLanguageDTO request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        user.updateLanguage(request.getLanguage());
    }

    /* 05-08 회원 탈퇴 */
    @Override
    public void withdraw(Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        user.withdraw(LocalDateTime.now());
    }
}
