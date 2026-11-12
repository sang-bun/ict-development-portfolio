package com.capstone.fertility.global.ai.mapping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FemaleLifestyleMappingTest {

    @Test
    void rawCigarettes10_mapsToTier2_andAiGets10() {
        FemaleLifestyleNormalized n = AiLifestyleCategoryMapper.normalizeFemaleSmokeInput(10, null);
        assertEquals(2, n.tier());
        assertEquals(10, n.rawQuantity());
        assertEquals(10, AiLifestyleCategoryMapper.femaleSmokeForAi(n.tier(), n.rawQuantity()));
    }

    @Test
    void rawBingeDays100_mapsToTier2_andAiGets100() {
        FemaleLifestyleNormalized n = AiLifestyleCategoryMapper.normalizeFemaleBingeInput(100, null);
        assertEquals(2, n.tier());
        assertEquals(100, n.rawQuantity());
        assertEquals(100, AiLifestyleCategoryMapper.femaleBingeForAi(n.tier(), n.rawQuantity()));
    }

    @Test
    void explicitCigarettesPerDay_takesPriorityOverSmokeLevel() {
        FemaleLifestyleNormalized n = AiLifestyleCategoryMapper.normalizeFemaleSmokeInput(0, 10);
        assertEquals(2, n.tier());
        assertEquals(10, n.rawQuantity());
    }

    @Test
    void tierOnly_usesRepresentativeValuesForAi() {
        assertEquals(0, AiLifestyleCategoryMapper.femaleSmokeForAi(0, null));
        assertEquals(3, AiLifestyleCategoryMapper.femaleSmokeForAi(1, null));
        assertEquals(10, AiLifestyleCategoryMapper.femaleSmokeForAi(2, null));
        assertEquals(13, AiLifestyleCategoryMapper.femaleBingeForAi(2, null));
    }

    @Test
    void displayLabels_fromRawFemaleInput() {
        assertEquals("하루 10개비", LifestyleDisplayLabels.smokeLabel(
                com.capstone.fertility.domain.user.enums.Gender.F, null, 2, 10));
        assertEquals("최근 1년 폭음(5잔+) 100일", LifestyleDisplayLabels.bingeLabel(
                com.capstone.fertility.domain.user.enums.Gender.F, null, 2, 100));
        assertEquals("주 1회 이상 음주", LifestyleDisplayLabels.drinkLabel("주 1회 이상"));
    }

    @Test
    void displayLabel_legacyDbRawInSmokeLevelColumn() {
        assertEquals("하루 10개비", LifestyleDisplayLabels.smokeLabel(
                com.capstone.fertility.domain.user.enums.Gender.F, null, 10, null));
    }

    @Test
    void resolveFemaleDrink_fromDrinkLevel2() {
        assertEquals("WEEKLY_OR_MORE", AiLifestyleCategoryMapper.resolveFemaleDrinkStatus(null, 2));
        assertEquals("월 1~3회", AiLifestyleCategoryMapper.toDrinkDisplayLabel(
                AiLifestyleCategoryMapper.resolveFemaleDrinkStatus(null, 1)));
    }

    @Test
    void resolveFemaleDrink_fromKoreanString() {
        assertEquals("MONTHLY_1_TO_3",
                AiLifestyleCategoryMapper.resolveFemaleDrinkStatus("월 1~3회", null));
    }

    @Test
    void legacyTierValues_stillWork() {
        FemaleLifestyleNormalized n = AiLifestyleCategoryMapper.normalizeFemaleSmokeInput(3, null);
        assertEquals(1, n.tier());
        assertNull(n.rawQuantity());
    }
}
