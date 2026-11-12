package com.capstone.fertility.global.apiPayLoad.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OauthSuccessCode implements BaseSuccessCode {

    KAKAO_LOGIN_SUCCESS(HttpStatus.OK, "OAUTH200","카카오 로그인 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
