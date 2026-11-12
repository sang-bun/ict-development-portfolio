package com.example.givy.global.infra.fcm.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FcmErrorCode implements BaseErrorCode {
    FCM_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "FCM503_1", "FCM 서버를 일시적으로 사용할 수 없습니다."),
    FCM_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM500_1", "푸시 알림 발송에 실패했습니다."),
    FCM_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "FCM400_1", "유효하지 않은 FCM 토큰입니다."),
    FCM_PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM404_1", "Firebase 프로젝트를 찾을 수 없거나 설정이 잘못되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
