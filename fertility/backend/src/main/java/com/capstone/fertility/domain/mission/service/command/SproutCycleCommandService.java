package com.capstone.fertility.domain.mission.service.command;

public interface SproutCycleCommandService {
    /**
     * 재검사 완료 후(명세): 꽃은 도감에 남기고 새싹 진행도만 Lv.1·EXP 0으로 초기화.
     */
    void resetAfterRetest(Long userId);
}
