package com.example.givy.domain.user.entity;

import com.example.givy.domain.notification.entity.Notification;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name="user_notification")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_notification_id")
    private Long userNotificationId;

    @Column(name="is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name="delivered_at", nullable = false)
    @CreatedDate
    private LocalDateTime deliveredAt;

    @Column(name="created_at", nullable = false)
    @CreatedDate
    private LocalDate createdAt;

    //mapping
    @ManyToOne
    @JoinColumn(name="notification_id")
    private Notification notification;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name="user_device_token_id")
    private UserDeviceToken userDeviceToken;

    public void readNotification(){
        this.isRead = true;
    }
}
