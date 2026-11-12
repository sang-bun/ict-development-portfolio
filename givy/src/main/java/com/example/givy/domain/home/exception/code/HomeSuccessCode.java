package com.example.givy.domain.home.exception.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HomeSuccessCode implements BaseSuccessCode {

    HOME_QUERY_SUCCESS(HttpStatus.OK, "HOME200", "홈 화면 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
