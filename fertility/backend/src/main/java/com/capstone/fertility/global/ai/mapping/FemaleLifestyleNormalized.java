package com.capstone.fertility.global.ai.mapping;

/**
 * 여성 흡연·폭음 입력 정규화 결과.
 *
 * @param tier         UI·DB용 0~2 단계
 * @param rawQuantity  설문 원시값(하루 개비 수 또는 연간 폭음 일수). 없으면 null
 */
public record FemaleLifestyleNormalized(int tier, Integer rawQuantity) {}
