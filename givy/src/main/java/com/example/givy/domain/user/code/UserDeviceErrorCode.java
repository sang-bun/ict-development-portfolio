package com.example.givy.domain.user.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserDeviceErrorCode implements BaseErrorCode {
    USER_DEVICE_TOKEN_NOT_FOUND_BY_USER_ID(HttpStatus.NOT_FOUND, "TOKEN404_1", "해당 사용자의 사용자 토큰을 찾을 수 없습니다."),;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
