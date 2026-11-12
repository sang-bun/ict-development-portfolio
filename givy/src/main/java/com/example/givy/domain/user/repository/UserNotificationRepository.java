package com.example.givy.domain.user.repository;

import com.example.givy.domain.notification.enums.NotificationType;
import com.example.givy.domain.user.entity.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    boolean existsByUsers_UserIdAndNotification_NotificationType(
            Long userId,
            NotificationType notificationType
    );

    @Query("SELECT un FROM UserNotification un " +
            "WHERE un.users.userId = :userId " +
            "AND (:isRead IS NULL OR un.isRead = :isRead)")
    Page<UserNotification> findAllByUserIdAndOptionalIsRead(
            @Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            Pageable pageable
    );
    Optional<UserNotification> findByUserNotificationId(Long userNotificationId);
}
