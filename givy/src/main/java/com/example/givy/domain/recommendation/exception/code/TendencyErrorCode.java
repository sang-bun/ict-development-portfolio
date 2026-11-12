package com.example.givy.domain.recommendation.exception.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TendencyErrorCode implements BaseErrorCode {

    TENDENCY_NOT_FOUND(HttpStatus.NOT_FOUND, "TENDENCY404_1", "존재하지 않는 성향 정보입니다."),
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "TENDENCY404_2", "생성된 추천 이력이 없습니다."),
    RECOMMENDATION_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "TENDENCY404_3", "해당 성향에 맞는 추천 상품을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}