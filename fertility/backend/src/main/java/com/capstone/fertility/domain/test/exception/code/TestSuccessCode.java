package com.capstone.fertility.domain.test.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TestSuccessCode implements BaseSuccessCode {

    TEST_SESSION_CREATED(HttpStatus.CREATED, "TEST200_1", "검사 세션이 생성되었습니다."),
    TEST_STEP_SAVED(HttpStatus.OK, "TEST200_2", "검사 단계가 임시 저장되었습니다."),
    TEST_SESSION_FETCHED(HttpStatus.OK, "TEST200_3", "검사 세션이 조회되었습니다."),
    TEST_SUBMITTED(HttpStatus.OK, "TEST200_4", "검사가 제출되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

