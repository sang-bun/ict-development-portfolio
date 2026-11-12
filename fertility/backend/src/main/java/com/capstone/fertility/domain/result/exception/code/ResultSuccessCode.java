package com.capstone.fertility.domain.result.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultSuccessCode implements BaseSuccessCode {

    RESULT_HISTORY_FETCHED(HttpStatus.OK, "RESULT200_1", "검사 결과 이력이 조회되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
