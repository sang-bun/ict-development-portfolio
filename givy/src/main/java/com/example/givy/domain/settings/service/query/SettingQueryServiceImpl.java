package com.example.givy.domain.settings.service.query;

import com.example.givy.domain.notification.entity.UserNotificationSetting;
import com.example.givy.domain.notification.enums.NotificationType;
import com.example.givy.domain.notification.repository.UserNotificationSettingRepository;
import com.example.givy.domain.settings.converter.SettingConverter;
import com.example.givy.domain.settings.dto.res.SettingResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingQueryServiceImpl implements SettingQueryService {

    private final UserNotificationSettingRepository userNotificationSettingRepository;

    /* 05-04 알림 설정 조회 */
    @Override
    public SettingResDTO.NotificationSettingDTO getNotificationSetting(Long userId) {

        boolean guideEnabled = userNotificationSettingRepository
                .findByUsers_UserIdAndNotificationType(
                        userId,
                        NotificationType.GUIDE
                )
                .map(UserNotificationSetting::getEnabled)
                .orElse(true); // 기본값 ON

        boolean challengeEnabled = userNotificationSettingRepository
                .findByUsers_UserIdAndNotificationType(
                        userId,
                        NotificationType.CHALLENGE
                )
                .map(UserNotificationSetting::getEnabled)
                .orElse(true);

        return SettingConverter.toNotificationSettingDTO(
                guideEnabled,
                challengeEnabled
        );
    }
}
