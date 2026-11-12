package com.example.givy.domain.kis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KisDailyPriceResponse { // 국내주식기간별시세 응답 DTO

    @JsonProperty("output") // 일별 데이터 리스트는 output에 담겨옴
    private List<DailyPrice> output;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyPrice {
        @JsonProperty("stck_bsop_date")
        private String date; // 일자 (YYYYMMDD)

        @JsonProperty("stck_clpr")
        private String closePrice; // 종가
    }
}
