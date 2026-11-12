package com.example.givy.domain.recommendation.service.query;

import com.example.givy.domain.recommendation.dto.res.TendencyResDTO;

public interface TendencyQueryService {
    TendencyResDTO.TendencyViewDTO getMyTendency(Long userId);
    TendencyResDTO.RecommendationListDTO getMyRecommendations(Long userId);
}
