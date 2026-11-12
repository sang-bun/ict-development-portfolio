package com.example.givy.domain.guide.code;

import com.example.givy.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GuideSuccessCode implements BaseSuccessCode {

    GUIDE_LIST_FETCHED(
            HttpStatus.OK,
            "GUIDE200_1",
            "가이드 목록 조회에 성공했습니다."
    ),

    GUIDE_DETAIL_FETCHED(
            HttpStatus.OK,
            "GUIDE200_2",
            "가이드 상세 조회에 성공했습니다."
    ),

    GUIDE_CATEGORY_LIST_FETCHED(
            HttpStatus.OK,
            "GUIDE200_3",
            "카테고리별 가이드 목록 조회에 성공했습니다."
    ),

    GUIDE_MY_STORED_LIST_FETCHED(
            HttpStatus.OK,
            "GUIDE200_4",
            "내가 저장한 가이드 조회에 성공했습니다."
    ),

    GUIDE_LIKED(
            HttpStatus.CREATED,
            "GUIDE201_1",
            "가이드 좋아요가 완료되었습니다."
    ),

    GUIDE_STORED(
            HttpStatus.CREATED,
            "GUIDE201_2",
            "가이드 저장이 완료되었습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
