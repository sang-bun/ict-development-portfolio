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
public class KisHolidayResponse {

    @JsonProperty("ctx_area_nk")
    private String ctxAreaNk;

    @JsonProperty("ctx_area_fk")
    private String ctxAreaFk;

    @JsonProperty("output")
    private List<Output> output;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        @JsonProperty("bass_dt")
        private String bassDt; // 기준일자 (YYYYMMDD)

        @JsonProperty("wday_dvsn_cd")
        private String wdayDvsnCd; // 요일구분코드 (01:일 ~ 07:토)

        @JsonProperty("biz_day_yn")
        private String bizDayYn; // 영업일여부 (Y/N)

        @JsonProperty("opnd_yn")
        private String opndYn; // 개장일여부 (Y:개장, N:휴장) - 핵심 데이터

        @JsonProperty("tr_day_yn")
        private String trDayYn; // 거래일여부 (Y/N)
    }
}
