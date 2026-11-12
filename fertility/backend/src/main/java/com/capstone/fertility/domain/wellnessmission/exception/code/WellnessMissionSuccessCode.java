package com.capstone.fertility.domain.wellnessmission.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WellnessMissionSuccessCode implements BaseSuccessCode {

    WELLNESS_MISSION_LIST_FETCHED(HttpStatus.OK, "WMISSION200_1", "내 웰니스 미션 목록을 조회했습니다."),
    WELLNESS_MISSION_COMPLETED(HttpStatus.OK, "WMISSION200_3", "웰니스 미션을 완료 처리했습니다."),
    WELLNESS_MISSION_TODAY_FETCHED(HttpStatus.OK, "WMISSION200_4", "오늘의 웰니스 미션을 조회했습니다."),
    WELLNESS_MISSION_HISTORY_FETCHED(HttpStatus.OK, "WMISSION200_5", "미션 성장 기록을 조회했습니다."),
    WELLNESS_MISSION_COLLECTIONS_FETCHED(HttpStatus.OK, "WMISSION200_6", "꽃 도감을 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
