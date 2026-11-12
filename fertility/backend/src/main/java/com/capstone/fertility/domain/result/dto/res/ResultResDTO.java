package com.capstone.fertility.domain.result.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ResultResDTO {

    @Builder
    public record ResultHistoryItemDTO(
            Long resultId,
            Long sessionId,
            Long userId,
            Integer aiScore,
            String riskLevel,
            @Schema(
                    description = "활성 위험요인 전체 목록(중요도 순). Top 3 고정이 아니며 길이는 0~N. " +
                            "비어있으면 위험 요인 없음 상태로 간주한다.",
                    example = "[\"흡연\", \"수면 부족\"]"
            )
            List<String> topFactors,
            LocalDateTime createdAt,
            /** 검사일(보관함·달력). createdAt과 동일 시각 */
            LocalDateTime inspectedAt,
            /** 보관함 카드용 한 줄 라벨 (예: "89점 · SAFE") */
            String label
    ) {}

    /**
     * 조회에 사용한 연·월과 해당 기간 결과 목록.
     */
    @Builder
    public record ResultHistoryDTO(
            int year,
            int month,
            List<ResultHistoryItemDTO> items
    ) {}
}
