package com.example.givy.domain.recommendation.controller;

import com.example.givy.domain.recommendation.dto.req.TendencyReqDTO;
import com.example.givy.domain.recommendation.dto.res.TendencyResDTO;
import com.example.givy.domain.recommendation.exception.code.TendencySuccessCode;
import com.example.givy.domain.recommendation.service.command.TendencyCommandService;
import com.example.givy.domain.recommendation.service.query.TendencyQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tendency")
public class TendencyController {

    private final TendencyCommandService tendencyCommandService;
    private final TendencyQueryService tendencyQueryService;

    @PostMapping
    public ApiResponse<TendencyResDTO.TendencyResultDTO> submitTendency(@AuthenticationPrincipal CustomPrincipal principal, @RequestBody @Valid TendencyReqDTO.TendencySurveyDTO request){

        Long userId = principal.getUserId();

        TendencyResDTO.TendencyResultDTO result = tendencyCommandService.submitTendency(userId, request);

        return ApiResponse.onSuccess(TendencySuccessCode.TENDENCY_SURVEY_SUCCESS, result);
    }

    @GetMapping("/me")
    public ApiResponse<TendencyResDTO.TendencyViewDTO> getMyQuery(@AuthenticationPrincipal CustomPrincipal principal) {

        Long userId = principal.getUserId();

        TendencyResDTO.TendencyViewDTO result = tendencyQueryService.getMyTendency(userId);

        return ApiResponse.onSuccess(TendencySuccessCode.TENDENCY_QUERY_SUCCESS, result);
    }

    @PostMapping("recommendations")
    public ApiResponse<TendencyResDTO.RecommendationResultDTO> createRecommendation(@AuthenticationPrincipal CustomPrincipal principal){

        Long userId = principal.getUserId();

        TendencyResDTO.RecommendationResultDTO result = tendencyCommandService.createRecommendation(userId);

        return ApiResponse.onSuccess(TendencySuccessCode.RECOMMENDATION_CREATED, result);
    }

    @GetMapping("/recommendations")
    public ApiResponse<TendencyResDTO.RecommendationListDTO> queryRecommendations(@AuthenticationPrincipal CustomPrincipal principal){

        Long userId = principal.getUserId();

        TendencyResDTO.RecommendationListDTO result = tendencyQueryService.getMyRecommendations(userId);

        return ApiResponse.onSuccess(TendencySuccessCode.RECOMMENDATION_LIST_QUERY_SUCCESS, result);
    }
}
