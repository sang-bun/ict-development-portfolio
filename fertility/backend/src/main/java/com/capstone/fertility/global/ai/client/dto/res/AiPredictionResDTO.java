package com.capstone.fertility.global.ai.client.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

/**
 * Python FastAPI AI 예측 서버 응답 DTO.
 * <p>
 * 응답 스펙(2026-04 변경 후):
 * <pre>
 * {
 *   "status": "success",
 *   "result": {
 *     "gender": "female",
 *     "score": 75,            // 또는 aiScore
 *     "risk_probability": 25.0,
 *     "bmi": 24.5,
 *     "top_factors": ["흡연", "수면 부족", "BMI 과다", ...]   // 활성 위험요인 전체(가변 길이, 정상/긍정 요인 제외)
 *   }
 * }
 * </pre>
 * <p>
 * 변경 이력:
 * <ul>
 *   <li>2026-04 이전: top1_factor / top2_factor / top3_factor, mission_candidates 필드 사용 → <b>제거됨</b>.</li>
 *   <li>2026-04 이후: 위험 요인은 {@code top_factors} 단일 가변 길이 배열로만 전달.</li>
 * </ul>
 * <p>
 * 정상/주의/위험 등급 매핑은 Spring 측 {@link com.capstone.fertility.domain.result.enums.RiskLevel}에서 score 기반으로 수행한다.
 * AI 서버는 score / risk_probability / top_factors 계산만 담당한다.
 * <p>
 * AI 서버가 더 이상 보내지 않는 레거시 키(top1_factor, mission_candidates 등)가 들어오더라도
 * {@link JsonIgnoreProperties}로 인해 파싱이 실패하지 않고 무시된다.
 */
public class AiPredictionResDTO {

    /**
     * 최상위 래퍼: status + result
     */
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("status") String status,
            @JsonProperty("result") ResultPayload result
    ) {}

    /**
     * result 객체. 필수 매핑 대상은 score, top_factors.
     * 알 수 없는 키는 모두 무시되어 향후 AI 서버가 새 필드를 추가해도 안전하다.
     */
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResultPayload(
            @JsonProperty("gender") String gender,

            /**
             * AI 점수(0~100). 서버 구현에 따라 score / aiScore / ai_score 중 하나로 내려올 수 있어 alias로 통일한다.
             */
            @JsonProperty("score")
            @JsonAlias({"aiScore", "ai_score"})
            Integer aiScore,

            /**
             * 위험 확률(%). 서버 구현에 따라 risk_probability / riskProbability로 내려올 수 있다.
             */
            @JsonProperty("risk_probability")
            @JsonAlias({"riskProbability"})
            Double riskProbability,

            /**
             * BMI 값.
             */
            @JsonProperty("bmi")
            Double bmi,

            /**
             * 활성 위험요인 전체 목록(가변 길이). 정상/긍정 요인은 AI 서버에서 사전 필터링되어 포함되지 않는다.
             * 위험 요인이 하나도 없으면 빈 배열([])로 내려오며, Spring은 이를 "위험 요인 없음" 상태로 간주한다.
             * <b>Top 3 고정이 아니다.</b> 길이는 0개~N개 모두 가능.
             */
            @JsonProperty("top_factors")
            @JsonAlias({"topFactors"})
            List<String> topFactors
    ) {
        public Integer resolvedAiScore() {
            return aiScore != null ? aiScore : 0;
        }

        /**
         * top_factors를 항상 non-null List로 반환한다(빈 배열 가능).
         * 호출 측은 size()로 "위험 요인 없음"(==0)을 판단하면 된다.
         */
        public List<String> resolvedTopFactors() {
            return topFactors != null ? topFactors : Collections.emptyList();
        }
    }
}
