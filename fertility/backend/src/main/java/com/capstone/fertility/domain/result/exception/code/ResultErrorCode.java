package com.capstone.fertility.domain.result.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultErrorCode implements BaseErrorCode {

    INVALID_YEAR_FOR_HISTORY(HttpStatus.BAD_REQUEST, "RESULT400_1", "조회 가능한 연도 범위가 아닙니다."),
    INVALID_MONTH_FOR_HISTORY(HttpStatus.BAD_REQUEST, "RESULT400_2", "월은 1~12 사이여야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
