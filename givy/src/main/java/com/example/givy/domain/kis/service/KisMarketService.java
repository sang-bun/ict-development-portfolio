package com.example.givy.domain.kis.service;

import com.example.givy.domain.kis.dto.KisHolidayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisMarketService {

    private final KisAuthService kisAuthService;
    private final WebClient webClient;

    @Value("${kis.api.url}")
    private String baseUrl;

    @Value("${kis.api.app-key}")
    private String appKey;

    @Value("${kis.api.app-secret}")
    private String appSecret;

    // 공통 URL 정리 메서드
    private String getCleanBaseUrl() {
        String cleanUrl = baseUrl;
        if (cleanUrl.endsWith("/")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
        }
        if (cleanUrl.endsWith("/oauth2/tokenP")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - "/oauth2/tokenP".length());
        }
        return cleanUrl;
    }

    // 국내 휴장일 조회 API (국내주식-040)
    public boolean isMarketOpen(LocalDate date) {
        String token = kisAuthService.getStoredToken();
        String dateString = date.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD

        log.info("휴장일 조회 요청: {}", dateString);

        try {
            String path = "/uapi/domestic-stock/v1/quotations/chk-holiday";

            URI uri = UriComponentsBuilder.fromHttpUrl(getCleanBaseUrl() + path)
                    .queryParam("BASS_DT", dateString)
                    .queryParam("CTX_AREA_NK", "")
                    .queryParam("CTX_AREA_FK", "")
                    .build()
                    .toUri();

            KisHolidayResponse response = webClient.get()
                    .uri(uri)
                    .header("authorization", "Bearer " + token)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "CTCA0903R") // 휴장일 조회 TR ID
                    .header("custtype", "P") // 개인
                    .retrieve()
                    .bodyToMono(KisHolidayResponse.class)
                    .block();

            if (response != null && response.getOutput() != null) {
                // 응답 리스트 중에서 요청한 날짜와 일치하는 데이터를 찾음
                return response.getOutput().stream()
                        .filter(output -> output.getBassDt().equals(dateString))
                        .findFirst()
                        .map(output -> "Y".equals(output.getOpndYn())) // Y면 개장일(true), N이면 휴장일(false)
                        .orElse(false);
            }

        } catch (Exception e) {
            log.error("휴장일 조회 중 오류 발생: {}", e.getMessage());
        }

        return false; // 오류 시 보수적으로 false(휴장) 반환 또는 예외 처리
    }
}
