package com.example.givy.domain.notification.service.command;

import com.example.givy.domain.commerce.enums.MarketCode;
import com.example.givy.domain.commerce.service.query.MarketHolidayQueryService;
import com.example.givy.domain.notification.code.NotificationErrorCode;
import com.example.givy.domain.notification.entity.Notification;
import com.example.givy.domain.notification.enums.NotificationType;
import com.example.givy.domain.notification.exception.NotificationException;
import com.example.givy.domain.notification.repository.NotificationRepository;
import com.example.givy.domain.user.converter.UserNotificationConverter;
import com.example.givy.domain.user.entity.UserDeviceToken;
import com.example.givy.domain.user.entity.UserNotification;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.repository.UserDeviceRepository;
import com.example.givy.domain.user.repository.UserNotificationRepository;
import com.example.givy.global.infra.fcm.dto.MultiNotificationRequest;
import com.example.givy.global.infra.fcm.service.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final MarketHolidayQueryService marketHolidayQueryService;
    private final UserDeviceRepository userDeviceRepository;
    private final FcmNotificationService fcmNotificationService;
    private final UserNotificationRepository userNotificationRepository;

    /* 마켓 오픈 시 푸시 알림 전송 */
    @Transactional()
    public void sendMarketOpenPush(MarketCode marketCode) {
        Notification notification = notificationRepository.findByNotificationType(NotificationType.MARKET_OPEN)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_TYPE_NOT_FOUND));

        //마켓 오픈 여부 확인
        if (!marketHolidayQueryService.isMarketOpenToday(marketCode)) return;

        List<UserDeviceToken> targets = userDeviceRepository.findAllByEnabledNotification(NotificationType.MARKET_OPEN);

        if (targets.isEmpty()) {
            log.info("발송 대상 유저가 존재하지 않아 알림 발송을 중단합니다. (Type: {})", NotificationType.CHALLENGE);
            return;
        }

        List<String> tokens = targets.stream()
                .map(UserDeviceToken::getDeviceToken)
                .toList();

        List<Users> users = targets.stream()
                .map(UserDeviceToken::getUsers)
                .toList();


        MultiNotificationRequest request = MultiNotificationRequest.of(tokens, notification.getTitle(), notification.getBody());

        fcmNotificationService.sendMessage(request);

        List<UserNotification> entities = targets.stream()
                .map(target -> UserNotificationConverter.toEntity(notification, target.getUsers(), target)
                ).toList();

        userNotificationRepository.saveAll(entities);

        log.info("[{}] 시장 알림 발송 성공 - {}명 대상", marketCode, entities.size());

    }

}