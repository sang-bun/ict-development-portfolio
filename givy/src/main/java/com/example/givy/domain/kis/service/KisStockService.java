package com.example.givy.domain.kis.service;

import com.example.givy.domain.kis.dto.KisDailyPriceResponse;
import com.example.givy.domain.kis.dto.KisEtfPriceResponse;
import com.example.givy.domain.kis.dto.KisStockInfoResponse;
import com.example.givy.domain.kis.dto.KisStockNameResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.util.retry.Retry;
import java.time.Duration;
import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisStockService {

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

    // 1. ETF/ETN 현재가 및 NAV 조회 (API 1: 068)
    public KisEtfPriceResponse getEtfPrice(String ticker) {
        String token = kisAuthService.getStoredToken();
        String path = "/uapi/domestic-stock/v1/quotations/inquire-price"; 

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(getCleanBaseUrl() + path)
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J") 
                    .queryParam("FID_INPUT_ISCD", ticker)      
                    .build()
                    .toUri();

            return webClient.get()
                    .uri(uri)
                    .header("authorization", "Bearer " + token)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "FHKST01010100") // 현재가 조회 TR ID
                    .header("custtype", "P")
                    .retrieve()
                    .bodyToMono(KisEtfPriceResponse.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))) // 1초 간격으로 최대 2회 재시도
                    .block();

        } catch (Exception e) {
            log.error("ETF 현재가 조회 실패 ({}): {}", ticker, e.getMessage());
            return null;
        }
    }

    // 2. 주식 기본조회 - 52주 데이터 (API 2: 067)
    public KisStockInfoResponse getStockBasicInfo(String ticker) {
        String token = kisAuthService.getStoredToken();
        String path = "/uapi/domestic-stock/v1/quotations/inquire-price"; 
        // 주의: 현재는 inquire-price를 쓰고 있어서 52주 데이터만 가져옴.
        // 종목명은 아래 getStockName() 사용 권장.

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(getCleanBaseUrl() + path)
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J") 
                    .queryParam("FID_INPUT_ISCD", ticker)      
                    .build()
                    .toUri();

            return webClient.get()
                    .uri(uri)
                    .header("authorization", "Bearer " + token)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "FHKST01010100") 
                    .header("custtype", "P")
                    .retrieve()
                    .bodyToMono(KisStockInfoResponse.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))) 
                    .block();

        } catch (Exception e) {
            log.error("주식 기본정보 조회 실패 ({}): {}", ticker, e.getMessage());
            return null;
        }
    }

    // 3. 기간별 시세(일별) 조회
    public KisDailyPriceResponse getDailyPrices(String ticker) {
        String token = kisAuthService.getStoredToken();
        String path = "/uapi/domestic-stock/v1/quotations/inquire-daily-price";

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(getCleanBaseUrl() + path)
                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                    .queryParam("FID_INPUT_ISCD", ticker)
                    .queryParam("FID_PERIOD_DIV_CODE", "D") // D:일봉
                    .queryParam("FID_ORG_ADJ_PRC", "1")     // 1:수정주가반영
                    .build()
                    .toUri();

            return webClient.get()
                    .uri(uri)
                    .header("authorization", "Bearer " + token)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "FHKST01010400") // 기간별 시세 TR ID
                    .header("custtype", "P")
                    .retrieve()
                    .bodyToMono(KisDailyPriceResponse.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))) // 1초 간격으로 최대 2회 재시도
                    .block();

        } catch (Exception e) {
            log.error("일별 시세 조회 실패 ({}): {}", ticker, e.getMessage());
            return null;
        }
    }

    // 4. 주식 기본정보 검색 (종목명 가져오기) - API 067 / CTPF1002R
    public String getStockName(String ticker) {
        String token = kisAuthService.getStoredToken();
        String path = "/uapi/domestic-stock/v1/quotations/search-stock-info";

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(getCleanBaseUrl() + path)
                    .queryParam("PRDT_TYPE_CD", "300") // 300: 주식/ETF
                    .queryParam("PDNO", ticker)        // 종목코드
                    .build()
                    .toUri();

            KisStockNameResponse response = webClient.get()
                    .uri(uri)
                    .header("authorization", "Bearer " + token)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "CTPF1002R") // 상품기본조회 TR ID
                    .header("custtype", "P")
                    .retrieve()
                    .bodyToMono(KisStockNameResponse.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
                    .block();

            if (response != null && response.getOutput() != null) {
                return response.getOutput().getStockName(); // 약어명 반환
            }
        } catch (Exception e) {
            log.error("종목명 조회 실패 ({}): {}", ticker, e.getMessage());
        }
        return null;
    }
}
