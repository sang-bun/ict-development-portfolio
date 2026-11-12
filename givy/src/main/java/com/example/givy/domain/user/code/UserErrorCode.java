package com.example.givy.domain.user.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NULL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "USER500_1", "null값으로 User 도메인을 찾아 터진 에러입니다."),
    USER_DUPLICATED_EMAIL(HttpStatus.CONFLICT, "USER409_1", "이미 사용 중인 이메일입니다."),
    USER_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER401_1", "비밀번호가 일치하지 않습니다."),
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "해당 이메일의 유저를 찾을 수 없습니다."),
    USER_INVALID_REFRESH_TOKEN_OWNER(
            HttpStatus.UNAUTHORIZED,
            "AUTH401_2",
            "본인 소유의 Refresh Token이 아닙니다."
    ),
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_2", "해당 id의 유저를 찾을 수 없습니다."),
    USER_SECURITIES_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_3", "사용자의 증권 계좌 정보를 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
