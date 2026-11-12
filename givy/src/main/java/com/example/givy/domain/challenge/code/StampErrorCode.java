package com.example.givy.domain.challenge.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StampErrorCode implements BaseErrorCode {
    STAMP_NOT_FOUND_BY_NAME(HttpStatus.NOT_FOUND, "STAMP404_1", "해당 이름을 가진 스탬프를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
