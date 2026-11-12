package com.capstone.fertility.domain.mission.service.reward;

import com.capstone.fertility.domain.mission.enums.FlowerType;
import lombok.Builder;

/**
 * 미션 완료 보상 결과.
 */
@Builder
public record RewardResult(
        int expGained,
        int currentExp,
        int currentLevel,
        boolean isLevelUp,
        FlowerType newFlower
) {}
