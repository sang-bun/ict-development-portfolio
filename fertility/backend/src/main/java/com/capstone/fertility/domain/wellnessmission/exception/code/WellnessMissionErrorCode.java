package com.capstone.fertility.domain.wellnessmission.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WellnessMissionErrorCode implements BaseErrorCode {

    WELLNESS_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "WMISSION404_1", "해당 웰니스 미션을 찾을 수 없습니다."),
    WELLNESS_MISSION_NOT_OWNER(HttpStatus.FORBIDDEN, "WMISSION403_1", "본인의 미션만 조회/완료할 수 있습니다."),
    WELLNESS_MISSION_EXPIRED(HttpStatus.GONE, "WMISSION410_1", "오늘의 미션이 아닙니다. 오늘의 미션 목록을 다시 불러오세요."),
    WELLNESS_MISSION_NOT_LATEST_RESULT(HttpStatus.BAD_REQUEST, "WMISSION400_3", "가장 최근 검사 결과의 미션만 완료할 수 있습니다."),
    WELLNESS_MISSION_NOT_OFFERED(HttpStatus.BAD_REQUEST, "WMISSION400_4", "현재 노출된 미션만 완료할 수 있습니다. 목록을 새로고침 하세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
