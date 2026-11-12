package com.example.givy.domain.settings.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SettingErrorCode implements BaseErrorCode {

    INVALID_CURRENT_PASSWORD(
            HttpStatus.BAD_REQUEST,
            "INVALID_CURRENT_PASSWORD",
            "현재 비밀번호가 일치하지 않습니다."
    ),

    PASSWORD_NOT_ALLOWED_FOR_SOCIAL_USER(
            HttpStatus.BAD_REQUEST,
            "PASSWORD_NOT_ALLOWED_FOR_SOCIAL_USER",
            "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다."
    ),

    TENDENCY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TENDENCY_NOT_FOUND",
            "초기화할 투자 성향 정보가 없습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
