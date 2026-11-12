package com.example.givy.domain.notification.repository;

import com.example.givy.domain.notification.entity.Notification;
import com.example.givy.domain.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByNotificationId(Long notificationId);
    Optional<Notification> findByNotificationType(NotificationType notificationType);
}
