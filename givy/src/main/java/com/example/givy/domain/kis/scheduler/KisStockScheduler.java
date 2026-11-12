package com.example.givy.domain.kis.scheduler;

import com.example.givy.domain.kis.dto.KisDailyPriceResponse;
import com.example.givy.domain.kis.dto.KisEtfPriceResponse;
import com.example.givy.domain.kis.dto.KisStockInfoResponse;
import com.example.givy.domain.kis.entity.StockHistory;
import com.example.givy.domain.kis.entity.StockInfo;
import com.example.givy.domain.kis.repository.StockHistoryRepository;
import com.example.givy.domain.kis.repository.StockInfoRepository;
import com.example.givy.domain.kis.service.KisMarketService;
import com.example.givy.domain.kis.service.KisStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KisStockScheduler {

    private final KisMarketService kisMarketService;
    private final KisStockService kisStockService;
    private final StockInfoRepository stockInfoRepository;
    private final StockHistoryRepository stockHistoryRepository;

    // 관리 종목 리스트 (나중에는 DB나 Config에서 관리)
    private final List<String> targetTickers = List.of(
            "005930", // 삼성전자
            "360750"  // TIGER 미국S&P500
    );

    // 매일 오후 4시 0분 0초에 실행 (장 마감 후)
    // 개발 중: 주석 처리하여 수동 실행만 사용 (POST /test/kis/trigger-scheduler)
    // 프로덕션 배포 시: 주석 해제하여 자동 실행 활성화
    // @Scheduled(cron = "0 0 16 * * *")
    @Transactional // 전체 메서드에도 트랜잭션 적용 (deleteByBaseDate 호출 때문)
    public void updateStockInfo() {
        LocalDate today = LocalDate.now();
        log.info("==================================================");
        log.info("[Scheduler] 스케줄러 실행 시작 (날짜: {})", today);
        log.info("==================================================");

        // 1. 휴장일 체크
        if (!kisMarketService.isMarketOpen(today)) {
            log.info("오늘은 휴장일입니다. 스케줄러를 종료합니다.");
            return;
        }

        // 2. 종목별 데이터 수집 및 저장
        // TODO: 향후 FHKST11300006(관심종목 시세) API를 활용해 한 번에 가져오는 방식으로 최적화 예정
        for (String ticker : targetTickers) {
            try {
                // 2-1. 종목 기본 정보 (현재가, NAV, 이름 등) 갱신 (트랜잭션 분리)
                processStockUpdate(ticker);
                Thread.sleep(500); // API 호출 간격 조절

                // 2-2. 시세 히스토리 (일봉) 저장 (트랜잭션 분리)
                processStockHistoryUpdate(ticker);

                // API 호출 제한 고려하여 약간의 텀을 둠 (1초)
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("스케줄러 인터럽트 발생");
            } catch (Exception e) {
                log.error("종목 갱신 실패 ({}): {}", ticker, e.getMessage());
            }
        }

        log.info("[Scheduler] 모든 작업 완료");
    }

    @Transactional // 개별 종목 처리에만 트랜잭션 적용
    protected void processStockUpdate(String ticker) {
        // API 1: 현재가, NAV
        KisEtfPriceResponse etfResponse = kisStockService.getEtfPrice(ticker);
        // API 2: 52주 최고/최저가
        KisStockInfoResponse stockInfoResponse = kisStockService.getStockBasicInfo(ticker);
        // API 3: 종목명 (상품기본조회)
        String stockName = kisStockService.getStockName(ticker);

        if (etfResponse == null || etfResponse.getOutput() == null || 
            stockInfoResponse == null || stockInfoResponse.getOutput() == null) {
            log.warn("데이터 수집 실패 (기본정보): {}", ticker);
            return;
        }

        KisEtfPriceResponse.Output etfOutput = etfResponse.getOutput();
        KisStockInfoResponse.Output infoOutput = stockInfoResponse.getOutput();

        // 파싱
        Long currentPrice = parseLong(etfOutput.getCurrentPrice());
        Double nav = parseDouble(etfOutput.getNav());
        Long highestPrice = parseLong(infoOutput.getHighestPrice52w());
        Long lowestPrice = parseLong(infoOutput.getLowestPrice52w());
        
        // 종목명 정제 (API 호출 결과 사용)
        String finalName = cleanStockName(stockName);

        // DB 저장 또는 업데이트
        stockInfoRepository.findById(ticker)
                .ifPresentOrElse(
                        existingStock -> {
                            existingStock.updatePrices(currentPrice, nav, highestPrice, lowestPrice, finalName);
                            log.info("업데이트 완료: {} ({}, 현재가: {})", ticker, finalName, currentPrice);
                        },
                        () -> {
                            StockInfo newStock = StockInfo.builder()
                                    .ticker(ticker)
                                    .name(finalName)
                                    .currentPrice(currentPrice)
                                    .nav(nav)
                                    .highestPrice52w(highestPrice)
                                    .lowestPrice52w(lowestPrice)
                                    .build();
                            stockInfoRepository.save(newStock);
                            log.info("신규 저장 완료: {} ({}, 현재가: {})", ticker, finalName, currentPrice);
                        }
                );
    }

    // 종목명 정제 메서드
    private String cleanStockName(String rawName) {
        if (rawName == null) return null;
        return rawName.replace("보통주", "")
                      .replace("증권상장지수투자신탁", "")
                      .replace("(주)", "")
                      .trim();
    }

    @Transactional // 개별 시세 저장에만 트랜잭션 적용
    protected void processStockHistoryUpdate(String ticker) {
        KisDailyPriceResponse response = kisStockService.getDailyPrices(ticker);

        if (response == null || response.getOutput() == null || response.getOutput().isEmpty()) {
            log.warn("데이터 수집 실패 (시세히스토리): {}", ticker);
            return;
        }

        // 효율성 개선: 전체 리스트가 아닌 '최신 데이터(0번 인덱스)' 하나만 가져와서 저장
        KisDailyPriceResponse.DailyPrice latestPrice = response.getOutput().get(0);

        try {
            LocalDate date = LocalDate.parse(latestPrice.getDate(), DateTimeFormatter.BASIC_ISO_DATE);
            Long closePrice = parseLong(latestPrice.getClosePrice());

            // 중복 저장 방지 (이미 해당 날짜 데이터가 있으면 스킵)
            if (stockHistoryRepository.findByTickerAndBaseDate(ticker, date).isPresent()) {
                log.info("이미 존재하는 시세 데이터입니다. ({} - {})", ticker, date);
                return;
            }

            StockHistory history = StockHistory.builder()
                    .ticker(ticker)
                    .baseDate(date)
                    .closePrice(closePrice)
                    .build();
            
            stockHistoryRepository.save(history);
            log.info("시세 히스토리 저장 완료: {} (날짜: {}, 종가: {})", ticker, date, closePrice);

        } catch (Exception e) {
            log.error("시세 저장 중 오류 ({}): {}", ticker, e.getMessage());
        }
    }

    // 유틸 메서드: String -> Long
    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return 0L;
        // 소수점이 포함될 수 있는 경우 처리 (반올림)
        if (value.contains(".")) {
             return Math.round(Double.parseDouble(value));
        }
        return Long.parseLong(value);
    }

    // 유틸 메서드: String -> Double
    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) return 0.0;
        return Double.parseDouble(value);
    }
}
