package com.example.givy.domain.guide.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GuideErrorCode implements BaseErrorCode {

    GUIDE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "GUIDE404_1",
            "해당 가이드를 찾을 수 없습니다."
    ),

    GUIDE_INVALID_CATEGORY(
            HttpStatus.BAD_REQUEST,
            "GUIDE400_1",
            "유효하지 않은 가이드 카테고리입니다."
    ),

    GUIDE_ALREADY_LIKED(
            HttpStatus.CONFLICT,
            "GUIDE409_1",
            "이미 좋아요한 가이드입니다."
    ),

    GUIDE_ALREADY_STORED(
            HttpStatus.CONFLICT,
            "GUIDE409_2",
            "이미 저장한 가이드입니다."
    ),

    GUIDE_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "GUIDE403_1",
            "해당 가이드에 대한 접근 권한이 없습니다."
    ),

    GUIDE_INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "GUIDE500_1",
            "가이드 처리 중 서버 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}

