package com.example.givy.domain.recommendation.dto.res;

import lombok.Builder;

import java.util.List;

public class TendencyResDTO {

    @Builder
    public record TendencyResultDTO(
            String investmentType,
            int scoreR,
            int scoreL,
            int scoreT,
            int totalScore,
            String imageBasicUrl,
            String riskLabel,        // 예: "안정추구"
            String periodLabel,      // 예: "단기"
            String familiarityLabel  // 예: "안정형"
    ){}

    @Builder
    public record TendencyViewDTO(
            String investmentType,
            String imageBasicUrl,
            String riskLabel,        // 예: "안정추구"
            String periodLabel,      // 예: "단기"
            String familiarityLabel  // 예: "안정형"
    ){}

    @Builder
    public record RecommendationResultDTO(
            String investmentType,
            String bestProductName,
            String bestProductCode
    ){}

    @Builder
    public record RecommendationListDTO(
            Long recommendationEventId,
            String investmentType,
            List<ProductOverviewDTO> products
    ){}

    @Builder
    public record ProductOverviewDTO(
            Long productId,
            String name,
            String code,
            String imageUrl,
            String tagline,
            String description,
            Double rateAvg
    ){}

}
