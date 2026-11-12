package com.example.givy.domain.user.service.command.notification;

import com.example.givy.domain.user.dto.res.UserNotificationResDTO;

public interface UserNotificationCommandService {
    UserNotificationResDTO.ReadUserNotification readNotification(Long userId, Long userNotificationId);
}
