package com.capstone.fertility.domain.report.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {

    RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404_1", "해당 검사 결과를 찾을 수 없습니다."),
    RESULT_NOT_OWNER(HttpStatus.FORBIDDEN, "REPORT403_1", "본인의 검사 결과만 조회할 수 있습니다."),
    LLM_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT500_1", "리포트 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() { return status; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
