package com.capstone.fertility.global.ai.mapping;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 프론트 toApiSmokeStatus / toApiDrinkStatus / toApiBingeStatus 와 동일한 코드 문자열 검증.
 */
class AiLifestyleCategoryMapperTest {

    @Test
    void smoke_frontendApiCodes() {
        assertThat(AiLifestyleCategoryMapper.toSmokeDisplayLabel("NEVER")).isEqualTo("안 피움");
        assertThat(AiLifestyleCategoryMapper.toSmokeDisplayLabel("OCCASIONAL")).isEqualTo("가끔 피움");
        assertThat(AiLifestyleCategoryMapper.toSmokeDisplayLabel("DAILY")).isEqualTo("매일 피움");
        assertThat(AiLifestyleCategoryMapper.mapSmokeStatusToAi("OCCASIONAL")).isEqualTo(1);
    }

    @Test
    void drink_frontendApiCodes() {
        assertThat(AiLifestyleCategoryMapper.toDrinkDisplayLabel("NEVER")).isEqualTo("안 마심");
        assertThat(AiLifestyleCategoryMapper.toDrinkDisplayLabel("MONTHLY_1_TO_3")).isEqualTo("월 1~3회");
        assertThat(AiLifestyleCategoryMapper.toDrinkDisplayLabel("WEEKLY_OR_MORE")).isEqualTo("주 1회 이상");
        assertThat(AiLifestyleCategoryMapper.mapDrinkStatusToAi("MONTHLY_1_TO_3")).isEqualTo(1);
    }

    @Test
    void binge_frontendApiCodes() {
        assertThat(AiLifestyleCategoryMapper.toBingeDisplayLabel("NEVER")).isEqualTo("없음");
        assertThat(AiLifestyleCategoryMapper.toBingeDisplayLabel("MONTHLY_1")).isEqualTo("월 1회");
        assertThat(AiLifestyleCategoryMapper.toBingeDisplayLabel("WEEKLY_OR_MORE")).isEqualTo("주 1회 이상");
    }
}
