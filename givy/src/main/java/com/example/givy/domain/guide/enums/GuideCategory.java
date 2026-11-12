package com.example.givy.domain.guide.enums;

import com.example.givy.domain.guide.code.GuideErrorCode;
import com.example.givy.domain.guide.exception.GuideException;

/* 문자열 검증을 Service에서 안하고 도메인에 위임*/
public enum GuideCategory {

    ETF,
    FUND,
    SAVING;

    public static GuideCategory from(String value) {
        if (value == null) {
            return null; // 선택형 필터 -> null 허용
        }

        try {
            return GuideCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GuideException(GuideErrorCode.GUIDE_INVALID_CATEGORY);
        }
    }
}
