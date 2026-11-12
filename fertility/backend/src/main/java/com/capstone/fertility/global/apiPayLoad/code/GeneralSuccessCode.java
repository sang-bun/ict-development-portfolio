package com.capstone.fertility.global.apiPayLoad.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode{

    OK(HttpStatus.OK, "SUCCESS200_1", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "CREATED201_1", "리소스가 성공적으로 생성되었습니다."),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "SUCCESS200_2", "토큰 갱신에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}