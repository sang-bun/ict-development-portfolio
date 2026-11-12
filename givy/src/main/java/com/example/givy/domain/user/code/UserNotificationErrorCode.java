package com.example.givy.domain.user.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserNotificationErrorCode implements BaseErrorCode {
    USER_NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOTIFICATION404_1", "사용자 알림을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
