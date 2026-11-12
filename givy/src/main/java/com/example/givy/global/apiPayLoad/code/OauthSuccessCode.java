package com.example.givy.global.apiPayLoad.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OauthSuccessCode implements BaseSuccessCode {

    KAKAO_LOGIN_SUCCESS(HttpStatus.OK, "OAUTH200_1","카카오 로그인 성공"),
    GOOGLE_LOGIN_SUCCESS(HttpStatus.OK, "OAUTH200_2","구글 로그인 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
