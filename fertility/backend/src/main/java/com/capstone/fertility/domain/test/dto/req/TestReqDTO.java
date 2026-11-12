package com.capstone.fertility.domain.test.dto.req;

import com.capstone.fertility.domain.user.enums.Gender;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TestReqDTO {

    /**
     * 0단계: 성별 선택(세션 생성용)
     */
    public record Start(
            @NotNull(message = "성별(gender)은 필수입니다.")
            Gender gender
    ) {}

    /**
     * 남성 전용 임시 저장 DTO
     * - step: 1~11
     * - 공통: age, height, weight, chlam, gon, sleepHours, sleepMinutes (수면 입력 시 둘 다 필수·합계 ≤ 24h)
     * - 남성 전용: numBioKid, sexFreq, hasSex12Mo, smokeStatus, drinkStatus, bingeStatus
     */
    public record MaleStepSave(
            @NotNull(message = "단계(step)는 필수입니다.")
            @Min(value = 1, message = "단계는 1 이상이어야 합니다.")
            @Max(value = 11, message = "남성 단계는 1~11 사이여야 합니다.")
            Integer step,

            Integer age,
            Double height,
            Double weight,
            Integer chlam,
            Integer gon,
            Integer sleepHours,
            Integer sleepMinutes,

            Integer numBioKid,
            Integer sexFreq,
            Boolean hasSex12Mo,
            String smokeStatus,
            String drinkStatus,
            String bingeStatus
    ) {}

    /**
     * 여성 전용 임시 저장 DTO
     * - step: 1~9
     * - 공통: age, height, weight, chlam, gon, sleepHours, sleepMinutes (수면 입력 시 둘 다 필수·합계 ≤ 24h)
     * - 여성 전용: menarcheAge, parity, pcos, endo, uf, pid, smokeLevel, binge12,
     *   drinkStatus(남성과 동일 한글 3종), cigarettesPerDay, bingeDaysPerYear
     * - smokeLevel/binge12에 0~2 tier 또는 설문 원시 숫자(개비·연간 일수) 가능
     */
    public record FemaleStepSave(
            @NotNull(message = "단계(step)는 필수입니다.")
            @Min(value = 1, message = "단계는 1 이상이어야 합니다.")
            @Max(value = 9, message = "여성 단계는 1~9 사이여야 합니다.")
            Integer step,

            Integer age,
            Double height,
            Double weight,
            Integer chlam,
            Integer gon,
            Integer sleepHours,
            Integer sleepMinutes,

            Integer menarcheAge,
            Integer parity,
            Integer pcos,
            Integer endo,
            Integer uf,
            Integer pid,
            Integer smokeLevel,
            Integer binge12,
            @JsonAlias({"drink_status", "drinkFrequency", "drink_frequency", "alcoholStatus"})
            String drinkStatus,
            /** 0=비음주, 1=월1~3회, 2=주1회이상 (drinkStatus 없을 때) */
            @JsonAlias({"drink_level", "drinkFrequency", "drink_frequency"})
            Integer drinkLevel,
            /** 하루 평균 개비 수 (smokeLevel 대신 또는 함께) */
            Integer cigarettesPerDay,
            /** 최근 1년 5잔+ 폭음 일수 (binge12 대신 또는 함께) */
            Integer bingeDaysPerYear,
            /** PSS 10문항 중간 저장 (0~4 × 10개, 선택적 — 미포함 시 기존 값 유지) */
            @Size(min = 10, max = 10, message = "PSS 문항은 정확히 10개여야 합니다.")
            List<Integer> pssAnswers
    ) {}

    /**
     * 최종 제출 DTO
     * - PSS 10문항 답변만 받음
     */
    public record Submit(
            @NotNull(message = "PSS 10문항 답변이 필요합니다.")
            @Size(min = 10, max = 10, message = "PSS 문항은 정확히 10개여야 합니다.")
            java.util.List<Integer> pssAnswers
    ) {}
}
