package com.example.givy.domain.user.service.command.notification;

import com.example.givy.domain.user.code.UserNotificationErrorCode;
import com.example.givy.domain.user.converter.UserNotificationConverter;
import com.example.givy.domain.user.dto.res.UserNotificationResDTO;
import com.example.givy.domain.user.entity.UserNotification;
import com.example.givy.domain.user.exception.UserNotificationException;
import com.example.givy.domain.user.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserNotificationCommandServiceImpl implements UserNotificationCommandService {
    private final UserNotificationRepository userNotificationRepository;

    /* 07-02 사용자 알림 읽음 처리 */
    @Override
    @Transactional
    public UserNotificationResDTO.ReadUserNotification readNotification(Long userId, Long userNotificationId) {
        UserNotification userNotification = userNotificationRepository.findByUserNotificationId(userNotificationId)
                .orElseThrow(() -> new UserNotificationException(UserNotificationErrorCode.USER_NOTIFICATION_NOT_FOUND));

        if (userNotification.getIsRead()) {
            return null;
        } else {
            userNotification.readNotification();
        }

        return UserNotificationConverter.toUserNotificationSimplyInfoDTO(userNotification);
    }
}
