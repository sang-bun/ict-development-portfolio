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
public class KisStockNameResponse {

    @JsonProperty("output")
    private Output output;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        @JsonProperty("prdt_abrv_name")
        private String stockName; // 상품 약어 명 (한글 종목명)
        
        @JsonProperty("prdt_name")
        private String stockFullName; // 상품 명 (전체 이름)
    }
}
