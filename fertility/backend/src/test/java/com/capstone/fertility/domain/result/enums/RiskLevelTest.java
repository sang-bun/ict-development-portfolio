package com.capstone.fertility.domain.result.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskLevelTest {

    @Test
    void determineLevel_thresholds_workAsExpected() {
        assertEquals(RiskLevel.DANGER, RiskLevel.determineLevel((Integer) null));
        assertEquals(RiskLevel.DANGER, RiskLevel.determineLevel(0));
        assertEquals(RiskLevel.DANGER, RiskLevel.determineLevel(49));
        assertEquals(RiskLevel.WARNING, RiskLevel.determineLevel(50));
        assertEquals(RiskLevel.WARNING, RiskLevel.determineLevel(79));
        assertEquals(RiskLevel.SAFE, RiskLevel.determineLevel(80));
        assertEquals(RiskLevel.SAFE, RiskLevel.determineLevel(100));
    }
}
