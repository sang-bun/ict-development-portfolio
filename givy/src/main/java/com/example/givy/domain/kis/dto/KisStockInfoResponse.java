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
public class KisStockInfoResponse {

    @JsonProperty("output")
    private Output output;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        @JsonProperty("w52_hgpr")
        private String highestPrice52w; // 52주 최고가

        @JsonProperty("w52_lwpr")
        private String lowestPrice52w; // 52주 최저가

        @JsonProperty("hts_kor_isnm")
        private String stockName; // HTS 한글 종목명
    }
}
