package com.capstone.fertility.domain.report.dto.res;

import lombok.Builder;

import java.util.List;

public class ReportResDTO {

    /**
     * 상세 리포트 응답.
     * LLM에서 정형 JSON으로 받아 섹션별로 매핑한다.
     */
    @Builder
    public record DetailReport(
            Long resultId,
            String nickname,
            Integer age,
            String gender,
            Integer score,
            String riskLevel,
            Intro intro,
            Condition condition,
            List<FactorAnalysis> factorAnalyses,
            List<Mission> missions,
            String closing,
            /** 프론트 상세 리포트 설문 요약(신체·생활습관). rows[].label / value */
            List<QuestionnaireGroup> questionnaireGroups,
            /** 동일 연령·성별 대비 표. item: BMI, 수면시간 등 */
            List<ComparisonRow> comparisonTable
    ) {}

    @Builder
    public record QuestionnaireGroup(
            String title,
            List<QuestionnaireRow> rows
    ) {}

    @Builder
    public record QuestionnaireRow(
            String label,
            String value
    ) {}

    @Builder
    public record ComparisonRow(
            String item,
            String myValue,
            String averageValue,
            String comparisonResult,
            /** higher | lower | neutral */
            String trend
    ) {}

    @Builder
    public record Intro(
            String greeting,
            String scoreMessage,
            String comfortMessage
    ) {}

    @Builder
    public record Condition(
            String sleepLabel,
            String stressLabel,
            String summary
    ) {}

    @Builder
    public record FactorAnalysis(
            String factor,
            String category,
            String mateThought,
            String expectedChange
    ) {}

    @Builder
    public record Mission(
            Long missionId,
            String title,
            String description,
            String linkedFactor,
            String category,
            Frequency frequency,
            Duration duration,
            String difficulty,
            Boolean userAdjustable
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
}
