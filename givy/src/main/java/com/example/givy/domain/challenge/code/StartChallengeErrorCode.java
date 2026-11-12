package com.example.givy.domain.challenge.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StartChallengeErrorCode implements BaseErrorCode {
    USER_CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE404_1", "사용자의 챌린지 참여 정보를 확인할 수 없습니다."),
    CHALLENGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "CHALLENGE409_1", "이미 참여한 챌린지입니다."),
    CHALLENGE_ALREADY_PROGRESSING(HttpStatus.CONFLICT, "CHALLENGE409_2", "참여 진행 중인 챌린지가 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
