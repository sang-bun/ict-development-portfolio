package com.capstone.fertility.global.ai.mapping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * fertility-ai-server male assemble_male_feature_dict 임계값을 Java로 재현해
 * Spring이 보내는 0~2 스케일이 AI에서 어떻게 해석되는지 문서화한다.
 *
 * @see fertility_inference_engine.py assemble_male_feature_dict (worktree)
 */
class SpringToAiMaleFeatureSimulationTest {

    /** AI: current_smoker = 1 if smoke_amount >= 2 */
    static boolean aiCurrentSmoker(int smoke30FromSpring) {
        return smoke30FromSpring >= 2;
    }

    /** AI: heavy_smoker = 1 if smoke_amount >= 4 */
    static boolean aiHeavySmoker(int smoke30FromSpring) {
        return smoke30FromSpring >= 4;
    }

    /** AI: frequent_drinker = 1 if drink_freq >= 5 */
    static boolean aiFrequentDrinker(int drink12FromSpring) {
        return drink12FromSpring >= 5;
    }

    /** AI: frequent_binge = 1 if binge_freq >= 4 */
    static boolean aiFrequentBinge(int binge12FromSpring) {
        return binge12FromSpring >= 4;
    }

    @Test
    void worstCaseSpringLabels_stillMissesAiDrinkAndBingeFlags() {
        int smoke = AiLifestyleCategoryMapper.mapSmokeStatusToAi("매일 피움");
        int drink = AiLifestyleCategoryMapper.mapDrinkStatusToAi("주 1회 이상");
        int binge = AiLifestyleCategoryMapper.mapBingeStatusToAi("주 1회 이상");

        assertEquals(2, smoke);
        assertEquals(2, drink);
        assertEquals(2, binge);

        assertTrue(aiCurrentSmoker(smoke), "매일 흡연만 current_smoker로 인정");
        assertFalse(aiHeavySmoker(smoke));
        assertFalse(aiFrequentDrinker(drink), "Spring 최대값 2 → AI 잦은 음주 미인정");
        assertFalse(aiFrequentBinge(binge), "Spring 최대값 2 → AI 잦은 폭음 미인정");
    }

    @Test
    void occasionalSmoker_springValue1_notSmokerInAi() {
        int smoke = AiLifestyleCategoryMapper.mapSmokeStatusToAi("가끔 피움");
        assertEquals(1, smoke);
        assertFalse(aiCurrentSmoker(smoke), "가끔 피움(1)은 AI에서 비흡연 취급");
    }

    @Test
    void wrongFrontendLabel_treatedAsNonSmokerNonDrinker() {
        assertEquals(0, AiLifestyleCategoryMapper.mapSmokeStatusToAi("흡연함"));
        assertEquals(0, AiLifestyleCategoryMapper.mapDrinkStatusToAi("자주 마심"));
    }
}
