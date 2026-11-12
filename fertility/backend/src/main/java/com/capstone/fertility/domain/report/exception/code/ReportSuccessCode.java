package com.capstone.fertility.domain.report.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportSuccessCode implements BaseSuccessCode {

    REPORT_GENERATED(HttpStatus.OK, "REPORT200_1", "상세 리포트가 생성되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
