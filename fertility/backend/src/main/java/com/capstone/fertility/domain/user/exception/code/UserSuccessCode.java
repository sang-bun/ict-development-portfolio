package com.capstone.fertility.domain.user.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {

    USER_SIGNUP_SUCCESS(HttpStatus.CREATED, "USER201_1", "회원가입이 성공적으로 완료되었습니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "USER200_1", "로그인이 성공적으로 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER200_2", "로그아웃이 성공적으로 완료되었습니다."),
    USER_TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "USER200_3", "토큰 재발급에 성공했습니다."),
    USER_FETCH_SUCCESS(HttpStatus.OK, "USER200_4", "유저 정보 조회에 성공했습니다."),
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "USER200_5", "프로필 수정에 성공했습니다."),

    // [파트너 연동 성공]
    USER_PARTNER_LINK_SUCCESS(HttpStatus.OK, "USER200_6", "파트너 연동에 성공했습니다."),
    USER_WITH_DRAW_SUCCESS(HttpStatus.OK, "USER200_7", "회원 탈퇴가 완료되었습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}