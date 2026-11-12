package com.example.givy.domain.recommendation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum InvestmentType {
    PARKING("PARKING_ETF", "파킹형 ETF"),
    ASSET("ASSET_ALLOC_ETF", "자산배분형 ETF"),
    FUND("FUND", "펀드형"),
    MARKET("MARKET_INDEX_ETF", "시장지수형 ETF"),
    THEME("THEME_ETF", "테마투자형");

    private final String code;
    private final String description; // 한글 값

    // 한글 설명(description)이나 코드(code), 혹은 Enum 이름(name)으로 찾는 메서드
    public static InvestmentType findByDescription(String description) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(description) || type.name().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown InvestmentType: " + description));
    }
}
