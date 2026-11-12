package com.capstone.fertility.domain.wellnessmission.support;

import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMission;

import java.time.LocalDateTime;

public final class WellnessMissionMapper {

    private WellnessMissionMapper() {}

    public static WellnessMissionResDTO.MissionItem toItem(WellnessMission e) {
        return toItem(e, e.isCompleted(), e.getCompletedAt());
    }

    /**
     * @param completed 현재 최신 검사·사이클 기준 완료 여부(또는 엔티티 플래그 대체)
     */
    public static WellnessMissionResDTO.MissionItem toItem(WellnessMission e, boolean completed, LocalDateTime completedAtOverride) {
        return WellnessMissionResDTO.MissionItem.builder()
                .missionId(e.getId())
                .resultId(e.getTestResult() != null ? e.getTestResult().getId() : null)
                .title(e.getTitle())
                .description(e.getDescription())
                .linkedFactor(e.getLinkedFactor())
                .category(e.getCategory() != null ? e.getCategory().name() : null)
                .frequency(WellnessMissionResDTO.Frequency.builder()
                        .type(e.getFrequencyType() != null ? e.getFrequencyType().name() : null)
                        .count(e.getFrequencyCount())
                        .unit(e.getFrequencyUnit())
                        .build())
                .duration(buildDuration(e.getDurationValue(), e.getDurationUnit()))
                .difficulty(e.getDifficulty() != null ? e.getDifficulty().name() : null)
                .userAdjustable(e.isUserAdjustable())
                .userAdjusted(e.isUserAdjusted())
                .completed(completed)
                .completedAt(completedAtOverride)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /**
     * value가 null 이거나 0 이하이면 duration 자체를 null 로 반환한다.
     * (시간 개념이 없는 미션이 0/없음으로 저장된 경우 클라이언트에 의미 없는 0을 내려보내지 않기 위함)
     */
    private static WellnessMissionResDTO.Duration buildDuration(Integer value, String unit) {
        if (value == null || value <= 0) {
            return null;
        }
        return WellnessMissionResDTO.Duration.builder()
                .value(value)
                .unit(unit)
                .build();
    }
}
