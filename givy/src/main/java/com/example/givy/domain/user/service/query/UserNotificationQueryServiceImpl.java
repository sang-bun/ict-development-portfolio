package com.example.givy.domain.user.service.query;

import com.example.givy.domain.user.converter.UserNotificationConverter;
import com.example.givy.domain.user.dto.res.UserNotificationResDTO;
import com.example.givy.domain.user.entity.UserNotification;
import com.example.givy.domain.user.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserNotificationQueryServiceImpl implements UserNotificationQueryService{
    private final UserNotificationRepository userNotificationRepository;

    @Override
    public UserNotificationResDTO.UserNotificationInfoListDTO getUserNotifications(Long userId, int page, int size, Boolean isRead) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserNotification> notifications = userNotificationRepository.findAllByUserIdAndOptionalIsRead(userId, isRead, pageable);

        return UserNotificationConverter.toUserNotificationInfoListDTO(notifications);
    }
}
