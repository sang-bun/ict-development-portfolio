package com.example.givy.domain.kis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KisEtfPriceResponse {

    @JsonProperty("output")
    private Output output;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        @JsonProperty("stck_prpr")
        private String currentPrice; // 현재가

        @JsonProperty("nav")
        private String nav; // 순자산가치 (NAV)

        @JsonProperty("hts_kor_isnm")
        private String stockName; // HTS 한글 종목명
    }
}
