package com.capstone.fertility.global.ai.client;

import com.capstone.fertility.global.ai.config.AiProperties;
import com.capstone.fertility.global.ai.client.dto.req.AiPredictionReqDTO;
import com.capstone.fertility.global.ai.client.dto.res.AiPredictionResDTO;
import com.capstone.fertility.global.apiPayLoad.code.GeneralErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;
import com.capstone.fertility.domain.user.enums.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiPredictionClientImpl implements AiPredictionClient {

    private static final String SUCCESS_STATUS = "success";

    @Qualifier("aiWebClient")
    private final WebClient webClient;
    private final AiProperties aiProperties;

    @Override
    public AiPredictionResDTO.Response predict(AiPredictionReqDTO.Request request, Gender gender) {
        if (gender == Gender.M) {
            return predictMale(request);
        }
        return predictFemale(request);
    }

    @Override
    public AiPredictionResDTO.Response predictMale(AiPredictionReqDTO.Request request) {
        String url = joinBaseUrlAndPath(aiProperties.getBaseUrl(), aiProperties.getPrediction().getPath().getMale());
        return callPredict(url, request);
    }

    @Override
    public AiPredictionResDTO.Response predictFemale(AiPredictionReqDTO.Request request) {
        String url = joinBaseUrlAndPath(aiProperties.getBaseUrl(), aiProperties.getPrediction().getPath().getFemale());
        return callPredict(url, request);
    }

    private AiPredictionResDTO.Response callPredict(String url, AiPredictionReqDTO.Request request) {
        try {
            AiPredictionResDTO.Response body = webClient.post()
                    .uri(url)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AiPredictionResDTO.Response.class)
                    .block();

            if (body == null) {
                log.error("AI 예측 응답 본문이 null 입니다. url={}", url);
                throw new GeneralException(GeneralErrorCode.AI_PREDICTION_SERVER_UNAVAILABLE);
            }
            if (body.status() == null || !SUCCESS_STATUS.equalsIgnoreCase(body.status().trim())) {
                log.error("AI 예측 응답 status가 success가 아닙니다. url={}, status={}", url, body.status());
                throw new GeneralException(GeneralErrorCode.AI_PREDICTION_SERVER_UNAVAILABLE);
            }
            if (body.result() == null) {
                log.error("AI 예측 응답 result가 null 입니다. url={}", url);
                throw new GeneralException(GeneralErrorCode.AI_PREDICTION_SERVER_UNAVAILABLE);
            }
            if (body.result().aiScore() == null) {
                log.error("AI 예측 응답 result.aiScore/score가 null 입니다. url={}", url);
                throw new GeneralException(GeneralErrorCode.AI_PREDICTION_SERVER_UNAVAILABLE);
            }

            return body;
        } catch (WebClientRequestException e) {
            log.error("AI 예측 서버 연결 실패: url={}", url, e);
            throw new GeneralException(GeneralErrorCode.AI_PREDICTION_SERVER_UNAVAILABLE);
        } catch (WebClientResponseException e) {
            // 응답 body에는 입력 데이터가 포함될 수 있어 민감정보 보호를 위해 로그에서 제외합니다.
            log.error("AI 예측 서버 HTTP 오류: status={}, url={}", e.getStatusCode(), url);
            throw new GeneralException(GeneralErrorCode.AI_PREDICTION_SERVER_UNAVAILABLE);
        }
    }

    private static String joinBaseUrlAndPath(String base, String path) {
        if (base == null) {
            base = "";
        }
        if (path == null) {
            path = "";
        }
        String b = base.trim();
        String p = path.trim();
        if (b.endsWith("/")) {
            b = b.substring(0, b.length() - 1);
        }
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return b + p;
    }
}
