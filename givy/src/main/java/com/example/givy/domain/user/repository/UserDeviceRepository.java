package com.example.givy.domain.user.repository;

import com.example.givy.domain.notification.enums.NotificationType;
import com.example.givy.domain.user.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByUsers_userIdAndDeviceId(Long userId, String deviceId);

    @Query("SELECT DISTINCT t FROM UserDeviceToken t " +
            "JOIN FETCH t.users u " +
            "JOIN UserNotificationSetting s ON s.users = u " +
            "WHERE s.enabled = true " +
            "AND s.notificationType = :notificationType")
    List<UserDeviceToken> findAllByEnabledNotification(
            @Param("notificationType") NotificationType notificationType
    );
}
