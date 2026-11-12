package com.capstone.fertility.domain.mission.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {

    SPROUT_RESET_REQUIRES_LEVEL_5(HttpStatus.BAD_REQUEST, "MISSION400_1", "재검사 사이클 초기화는 새싹 Lv.5(꽃 달성) 상태에서만 가능합니다.");

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
