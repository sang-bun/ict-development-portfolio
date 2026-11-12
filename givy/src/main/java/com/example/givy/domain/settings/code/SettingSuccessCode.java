package com.example.givy.domain.settings.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SettingSuccessCode implements BaseSuccessCode {

    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "PROFILE_UPDATE_SUCCESS", "프로필 수정 성공"),
    PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "PASSWORD_UPDATE_SUCCESS", "비밀번호 변경 성공"),
    TENDENCY_RESET_SUCCESS(HttpStatus.OK, "TENDENCY_RESET_SUCCESS", "투자 성향 재설정 성공"),

    NOTIFICATION_SETTING_FETCH_SUCCESS(
            HttpStatus.OK,
            "NOTIFICATION_SETTING_FETCH_SUCCESS",
            "알림 설정 조회 성공"
    ),
    NOTIFICATION_SETTING_UPDATE_SUCCESS(
            HttpStatus.OK,
            "NOTIFICATION_SETTING_UPDATE_SUCCESS",
            "알림 수신 설정 변경 성공"
    ),

    LANGUAGE_UPDATE_SUCCESS(HttpStatus.OK, "LANGUAGE_UPDATE_SUCCESS", "앱 언어 변경 성공"),
    USER_WITHDRAW_SUCCESS(HttpStatus.OK, "USER_WITHDRAW_SUCCESS", "회원 탈퇴 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
