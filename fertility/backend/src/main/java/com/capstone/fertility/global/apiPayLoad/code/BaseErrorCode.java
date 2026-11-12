package com.capstone.fertility.global.apiPayLoad.code;

import org.springframework.http.HttpStatus;

//API 응답 통일 - 성공/실패 응답 중 실패 응답을 통일하기 위함
public interface BaseErrorCode {
    //HTTP 상태 코드, 에러 코드 명시, 메시지를 넣어줄 수 있음.
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
