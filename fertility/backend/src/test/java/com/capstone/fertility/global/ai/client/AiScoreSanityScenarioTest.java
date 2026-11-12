package com.capstone.fertility.global.ai.client;

import com.capstone.fertility.domain.result.enums.RiskLevel;
import com.capstone.fertility.global.ai.client.dto.res.AiPredictionResDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 주의:
 * 이 테스트는 "실제 FastAPI 모델 추론값"을 검증하는 테스트가 아니라,
 * Spring이 전달받은 점수를 해석/검증하는 안전 가드 테스트입니다.
 */
class AiScoreSanityScenarioTest {

    @Test
    void healthyProfile_shouldHaveHigherScoreThanRiskyProfile_inExpectedScenario() {
        // 건강한 시나리오(예시): 비흡연, 비음주, 충분한 수면, 낮은 스트레스
        AiPredictionResDTO.ResultPayload healthy = AiPredictionResDTO.ResultPayload.builder()
                .gender("female")
                .aiScore(93)
                .riskProbability(6.0)
                .bmi(21.8)
                .topFactors(List.of())
                .build();

        // 고위험 시나리오(예시): 흡연/음주/수면부족/높은 스트레스
        AiPredictionResDTO.ResultPayload risky = AiPredictionResDTO.ResultPayload.builder()
                .gender("male")
                .aiScore(34)
                .riskProbability(78.0)
                .bmi(30.2)
                .topFactors(List.of("흡연", "음주", "수면 부족", "높은 스트레스"))
                .build();

        // sanity: 건강 시나리오 점수가 고위험 시나리오보다 반드시 높아야 한다.
        assertTrue(healthy.resolvedAiScore() > risky.resolvedAiScore());
        assertEquals(RiskLevel.SAFE, RiskLevel.determineLevel(healthy.resolvedAiScore()));
        assertEquals(RiskLevel.DANGER, RiskLevel.determineLevel(risky.resolvedAiScore()));
    }

    @Test
    void emptyTopFactors_meansNoActiveRiskFactor() {
        AiPredictionResDTO.ResultPayload payload = AiPredictionResDTO.ResultPayload.builder()
                .aiScore(88)
                .topFactors(List.of())
                .build();

        assertTrue(payload.resolvedTopFactors().isEmpty());
        assertEquals(RiskLevel.SAFE, RiskLevel.determineLevel(payload.resolvedAiScore()));
    }
}
