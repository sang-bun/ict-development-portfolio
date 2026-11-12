package com.capstone.fertility.domain.mission.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class MissionResDTO {

    @Builder
    public record MissionHistoryItemDTO(
            @Schema(description = "이벤트 발생 시각(ISO-8601 로컬)", example = "2026-05-07T14:30:00")
            String date,
            @Schema(description = "이벤트 종류", example = "MISSION_COMPLETE")
            String action,
            @Schema(description = "경험치 변화 문자열", example = "+5")
            String expChange
    ) {}

    @Builder
    public record MissionHistoryDTO(
            List<MissionHistoryItemDTO> items,
            @Schema(description = "다음 페이지 조회 시 쿼리 lastLogId로 넣을 값. 없으면 더 이상 과거 기록이 없음.")
            Long nextLastLogId
    ) {}

    @Builder
    public record FlowerCollectionItemDTO(
            @Schema(description = "꽃 종류 코드(PEONY / BABYS_BREATH / LOTUS 중 하나)", example = "PEONY")
            String flowerType,
            @Schema(description = "획득(최종 진화) 시각 ISO-8601", example = "2026-05-01T10:00:00")
            String achievedAt
    ) {}
}
