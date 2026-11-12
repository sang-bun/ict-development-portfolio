package com.example.givy.domain.user.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {

    USER_SIGNUP_CREATED(HttpStatus.CREATED, "USER201_1", "회원가입이 성공적으로 완료되었습니다."),
    USER_SOCIAL_SIGNUP_CREATED(HttpStatus.CREATED, "USER201_2", "프로필 완성하여 소셜 회원가입이 성공적으로 완료되었습니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "USER200_1", "로그인이 성공적으로 완료되었습니다."),
    USER_FETCH_SUCCESS(HttpStatus.OK, "USER200_2", "유저 정보 조회 성공"),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER201_3", "로그아웃이 성공적으로 완료되었습니다."),
    USER_TOKEN_REFRESH_SUCCESS(
            HttpStatus.OK,
            "USER200_3",
            "토큰 재발급에 성공했습니다."
    ),

    USER_SECURITIES_CREATED(HttpStatus.CREATED, "USER201_2", "증권 계좌 등록이 완료되었습니다."),
    SOME_USER_SECURITIES_CREATED(HttpStatus.CREATED, "USER201_3", "이미 등록된 계좌를 제외하고 계좌 등록을 완료했습니다."),
    USER_SECURITIES_ALREADY_CREATED(HttpStatus.OK, "USER200_4", "이미 등록된 계좌입니다."),
    USER_ADULT_CHECK_SUCCESS(HttpStatus.OK, "USER200_5", "성인 여부 검증이 완료되었습니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
