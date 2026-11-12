package com.example.givy.domain.user.dto.res;

import com.example.givy.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserNotificationResDTO {

    @Getter
    @Builder
    public static class UserNotificationInfoDTO{
        private Long userNotificationId;
        private Long notificationId;
        private NotificationType notificationType;
        private String title;
        private String body;
        private String targetType;
        private String targetId;
        private Boolean isRead;
        private LocalDateTime deliveredAt;
        private LocalDate createdAt;
    }

    @Getter
    @Builder
    public static class UserNotificationInfoListDTO{
        private List<UserNotificationInfoDTO> userNotificationInfo;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Getter
    @Builder
    public static class ReadUserNotification{
        private Long userNotificationId;
        private Boolean isRead;
    }
}
