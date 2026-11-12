package com.example.givy.global.infra.fcm.dto;

import com.example.givy.domain.notification.enums.NotificationType;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleNotificationRequest implements FcmMessageDto {

    private String targetToken; //기기 토큰
    private String title;
    private String body;
    private NotificationType notificationType;

    @Builder(access = AccessLevel.PRIVATE)
    private SingleNotificationRequest(@NonNull String targetToken, String title, String body) {
        this.targetToken = targetToken;
        this.title = title;
        this.body = body;
    }

    public static SingleNotificationRequest of(String token, String title, String body) {
        return SingleNotificationRequest.builder()
                .targetToken(token)
                .title(title)
                .body(body)
                .build();
    }

    // 인터페이스(NotificationRequest) 메소드 구현
    @Override
    public String title() { return title; }

    @Override
    public String body() { return body; }

    public Message.Builder buildMessage() {
        return Message.builder()
                .setToken(targetToken)
                .setNotification(toNotification());
    }

    @Override
    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}