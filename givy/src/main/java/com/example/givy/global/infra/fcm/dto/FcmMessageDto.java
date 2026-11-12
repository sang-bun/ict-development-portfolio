package com.example.givy.global.infra.fcm.dto;

import com.example.givy.domain.notification.enums.NotificationType;
import com.google.firebase.messaging.Notification;

import java.util.List;

//FCM으로 보내는 요청 DTO
//이 데이터를 가지고 알림 발송
//단일, 다중 사용자 알림 발송을 위한 인터페이스
public interface FcmMessageDto {
    String title();
    String body();
    Notification toNotification();
}