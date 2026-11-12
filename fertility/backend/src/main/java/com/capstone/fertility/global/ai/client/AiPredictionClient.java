package com.capstone.fertility.global.ai.client;

import com.capstone.fertility.global.ai.client.dto.req.AiPredictionReqDTO;
import com.capstone.fertility.global.ai.client.dto.res.AiPredictionResDTO;
import com.capstone.fertility.domain.user.enums.Gender;

/**
 * Python FastAPI AI 예측 서버와 통신하는 클라이언트.
 * 남성/여성별 엔드포인트 분기.
 */
public interface AiPredictionClient {

    /**
     * 세션 데이터를 AI 서버로 전송하고 예측 결과(status, result.score, result.top_factors 등)를 받습니다.
     *
     * @param request 세션 기반 예측 요청 DTO
     * @param gender  남성(M) / 여성(F) 모델 엔드포인트 분기용
     * @return AI 예측 최상위 응답 래퍼 ({@code status}, {@code result})
     */
    AiPredictionResDTO.Response predict(AiPredictionReqDTO.Request request, Gender gender);

    /**
     * 남성 모델 엔드포인트 고정 호출
     */
    AiPredictionResDTO.Response predictMale(AiPredictionReqDTO.Request request);

    /**
     * 여성 모델 엔드포인트 고정 호출
     */
    AiPredictionResDTO.Response predictFemale(AiPredictionReqDTO.Request request);
}
