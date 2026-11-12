package com.example.givy.domain.user.service.query;

import com.example.givy.domain.user.dto.res.UserNotificationResDTO;

public interface UserNotificationQueryService {
    UserNotificationResDTO.UserNotificationInfoListDTO getUserNotifications(Long userId, int page, int size, Boolean isRead);
}
