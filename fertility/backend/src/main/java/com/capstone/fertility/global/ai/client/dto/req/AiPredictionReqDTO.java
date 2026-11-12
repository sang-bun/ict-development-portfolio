package com.capstone.fertility.global.ai.client.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * AI 예측 서버로 전송하는 요청 DTO 래퍼 (record 기반).
 * <p>
 * JSON 직렬화 필드명: age, height, weight, menarche_age, parity, pcos, endo, uf, pid, chlam, gon,
 * SMOKE30, DRINK12, BINGE12, num_bio_kid, sex_freq, has_sex_12mo, sleep_hours, stress_score, stress_level
 * ({@code sleep_hours}: 시·분을 합산한 하루 수면 시간(시간 단위 소수). Python에서 sleep/stress 미사용 시 무시 가능)
 */
public class AiPredictionReqDTO {

    @Builder
    public record Request(
            @JsonProperty("age")
            Integer age,

            @JsonProperty("height")
            Double height,

            @JsonProperty("weight")
            Double weight,

            @JsonProperty("menarche_age")
            Integer menarcheAge,

            @JsonProperty("parity")
            Integer parity,

            @JsonProperty("pcos")
            Integer pcos,

            @JsonProperty("endo")
            Integer endo,

            @JsonProperty("uf")
            Integer uf,

            @JsonProperty("pid")
            Integer pid,

            @JsonProperty("chlam")
            Integer chlam,

            @JsonProperty("gon")
            Integer gon,

            // 생활 습관/행동 관련 정량화된 특성
            @JsonProperty("SMOKE30")
            Integer smoke30,

            @JsonProperty("DRINK12")
            Integer drink12,

            @JsonProperty("BINGE12")
            Integer binge12Score,

            // 남성/공통 추가 필드
            @JsonProperty("num_bio_kid")
            Integer numBioKid,

            @JsonProperty("sex_freq")
            Integer sexFreq,

            @JsonProperty("has_sex_12mo")
            Integer hasSex12Mo,

            @JsonProperty("sleep_hours")
            Double sleepHours,

            @JsonProperty("stress_score")
            Integer stressScore,

            @JsonProperty("stress_level")
            String stressLevel
    ) {}
}
