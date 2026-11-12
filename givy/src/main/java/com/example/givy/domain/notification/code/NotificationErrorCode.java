package com.example.givy.domain.notification.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {
    NOTIFICATION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI404_1", "해당 타입의 알림을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
