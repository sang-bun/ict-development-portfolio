package com.capstone.fertility.domain.result.converter;

import com.capstone.fertility.domain.result.dto.res.ResultResDTO;
import com.capstone.fertility.domain.result.entity.TestResult;

import java.util.Collections;

public final class ResultConverter {

    private ResultConverter() {}

    public static ResultResDTO.ResultHistoryItemDTO toHistoryItemDTO(TestResult entity) {
        return ResultResDTO.ResultHistoryItemDTO.builder()
                .resultId(entity.getId())
                .sessionId(entity.getTestSession() != null ? entity.getTestSession().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .aiScore(entity.getAiScore())
                .riskLevel(entity.getRiskLevel() != null ? entity.getRiskLevel().name() : null)
                .topFactors(entity.getTopFactors() != null
                        ? entity.getTopFactors()
                        : Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .inspectedAt(entity.getCreatedAt())
                .label(buildHistoryLabel(entity))
                .build();
    }

    private static String buildHistoryLabel(TestResult entity) {
        Integer score = entity.getAiScore();
        String risk = entity.getRiskLevel() != null ? entity.getRiskLevel().name() : null;
        if (score == null && risk == null) {
            return null;
        }
        if (score != null && risk != null) {
            return score + "점 · " + risk;
        }
        if (score != null) {
            return score + "점";
        }
        return risk;
    }
}
