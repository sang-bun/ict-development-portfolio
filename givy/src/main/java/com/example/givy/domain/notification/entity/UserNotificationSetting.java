package com.example.givy.domain.notification.entity;

import com.example.givy.domain.notification.enums.NotificationType;
import com.example.givy.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_notification_setting",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "notification_type"}
                )
        }
)
public class UserNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notification_setting_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    // mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    /* 알림 설정 변경 */
    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
