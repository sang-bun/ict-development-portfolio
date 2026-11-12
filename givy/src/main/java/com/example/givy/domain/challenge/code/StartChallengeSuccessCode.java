package com.example.givy.domain.challenge.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StartChallengeSuccessCode implements BaseSuccessCode {
    START_CHALLENGE_CREATED(HttpStatus.CREATED, "CHALLENGE201_1", "챌린지 참여가 성공적으로 이루어졌습니다."),
    START_CHALLENGE_COMPLETED(HttpStatus.OK, "CHALLENGE200_1", "스타트 챌린지 및 도장 지급이 완료되었습니다."),
    START_CHALLENGE_STATUS_FOUND(HttpStatus.OK, "CHALLENGE200_2", "스타트 챌린지 상태 조회를 성공하였습니다."),
    START_CHALLENGE_NOT_FOUND(HttpStatus.OK, "CHALLENGE200_3", "사용자의 스타트 챌린지를 찾을 수 없습니다."),
    ALREADY_COMPLETED_START_CHALLENGE(HttpStatus.OK, "CHALLENGE200_4", "이미 완료한 챌린지입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
