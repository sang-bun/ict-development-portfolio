package com.capstone.fertility.domain.wellnessmission.dto.res;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class WellnessMissionResDTO {

    @Builder
    public record MissionItem(
            Long missionId,
            Long resultId,
            String title,
            String description,
            String linkedFactor,
            String category,
            Frequency frequency,
            Duration duration,
            String difficulty,
            Boolean userAdjustable,
            Boolean userAdjusted,
            Boolean completed,
            LocalDateTime completedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    @Builder
    public record CompleteResult(
            Long missionId,
            int expGained,
            int currentExp,
            int currentLevel,
            int requiredExpForCurrentLevel,
            boolean isLevelUp,
            boolean alreadyCompleted,
            boolean dailyRewardCapReached,
            String newFlower
    ) {}

    @Builder
    public record Frequency(
            String type,
            Integer count,
            String unit
    ) {}

    @Builder
    public record Duration(
            Integer value,
            String unit
    ) {}

    @Builder
    public record MyMissions(
            int total,
            List<MissionItem> missions
    ) {}
}
