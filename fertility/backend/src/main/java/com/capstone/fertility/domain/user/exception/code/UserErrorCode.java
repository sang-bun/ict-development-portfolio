package com.capstone.fertility.domain.user.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NULL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "USER500_1", "null값으로 User 도메인을 찾아 터진 에러입니다."),
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "해당 ID의 유저를 찾을 수 없습니다."),
    USER_KAKAO_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_2", "해당 카카오 ID로 가입된 유저가 없습니다."),

    // [이메일 로그인 관련 추가]
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_4", "가입되지 않은 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401_3", "비밀번호가 일치하지 않습니다."),
    EMAIL_LOGIN_NOT_SUPPORTED_FOR_SOCIAL(HttpStatus.BAD_REQUEST, "AUTH400_4", "카카오 로그인으로 가입한 계정입니다. 카카오 로그인을 이용해 주세요."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409_1", "이미 가입된 이메일입니다."), // U002에서 수정

    // [인증/토큰 관련]
    USER_INVALID_REFRESH_TOKEN_OWNER(HttpStatus.UNAUTHORIZED, "AUTH401_2", "본인 소유의 Refresh Token이 아닙니다."),
    USER_WITHDRAWN(HttpStatus.UNAUTHORIZED, "USER401_4", "탈퇴한 계정입니다."),

    // [파트너 연동 관련]
    PARTNER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_3", "연동할 파트너를 찾을 수 없습니다."),
    PARTNER_ALREADY_LINKED(HttpStatus.BAD_REQUEST, "USER400_1", "이미 파트너와 연동되어 있습니다."),
    INVALID_PARTNER_CODE(HttpStatus.BAD_REQUEST, "USER400_2", "유효하지 않은 파트너 연결 코드입니다."),
    INVALID_DISPLAY_NAME(HttpStatus.BAD_REQUEST, "USER400_3", "표시 이름은 1자 이상 20자 이하여야 합니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "USER400_4", "성별은 M 또는 F만 가능합니다.");

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