package com.example.givy.domain.notification.repository;

import com.example.givy.domain.notification.entity.UserNotificationSetting;
import com.example.givy.domain.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationSettingRepository
        extends JpaRepository<UserNotificationSetting, Long> {

    Optional<UserNotificationSetting>
    findByUsers_UserIdAndNotificationType(
            Long userId,
            NotificationType notificationType
    );
}
