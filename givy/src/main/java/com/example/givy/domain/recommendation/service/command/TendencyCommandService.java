package com.example.givy.domain.recommendation.service.command;

import com.example.givy.domain.recommendation.dto.req.TendencyReqDTO;
import com.example.givy.domain.recommendation.dto.res.TendencyResDTO;

public interface TendencyCommandService {
    TendencyResDTO.TendencyResultDTO submitTendency(Long userId, TendencyReqDTO.TendencySurveyDTO request);
    TendencyResDTO.RecommendationResultDTO createRecommendation(Long userId);
}
