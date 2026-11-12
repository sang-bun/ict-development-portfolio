package com.example.givy.domain.user.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserNotificationSuccessCode implements BaseSuccessCode {
    USER_NOTIFICATION_LIST_FOUND(HttpStatus.OK, "USER_NOTIFICATION200_1", "유저 알림 목록 조회에 성공했습니다."),
    USER_NOTIFICATION_MARKED_AS_READ(HttpStatus.OK, "USER_NOTIFICATION200_2", "유저 알림을 읽음 처리했습니다."),
    USER_NOTIFICATION_ALREADY_READ(HttpStatus.OK, "USER_NOTIFICATION200_3", "이미 읽은 알림입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
