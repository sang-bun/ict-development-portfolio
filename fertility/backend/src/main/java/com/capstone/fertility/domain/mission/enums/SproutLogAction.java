package com.capstone.fertility.domain.mission.enums;

/**
 * 새싹 성장 기록(히스토리)에 남기는 이벤트 유형.
 */
public enum SproutLogAction {
    MISSION_COMPLETE,
    /** 2일 이상 미수행 등 미접속 페널티 */
    INACTIVITY_PENALTY
}
