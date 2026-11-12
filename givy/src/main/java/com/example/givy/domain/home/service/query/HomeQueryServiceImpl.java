package com.example.givy.domain.home.service.query;

import com.example.givy.domain.challenge.entity.StartChallenge;
import com.example.givy.domain.challenge.enums.Status;
import com.example.givy.domain.challenge.repository.ChallengeRepository;
import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.domain.commerce.repository.ProductRepository;
import com.example.givy.domain.home.converter.HomeConverter;
import com.example.givy.domain.home.dto.res.HomeResDTO;
import com.example.givy.domain.kis.dto.KisDailyPriceResponse;
import com.example.givy.domain.kis.dto.KisEtfPriceResponse;
import com.example.givy.domain.kis.dto.KisStockInfoResponse;
import com.example.givy.domain.kis.service.KisStockService;
import com.example.givy.domain.recommendation.entity.RecommendationEvent;
import com.example.givy.domain.recommendation.entity.Tendency;
import com.example.givy.domain.recommendation.exception.TendencyException;
import com.example.givy.domain.recommendation.exception.code.TendencyErrorCode;
import com.example.givy.domain.recommendation.repository.RecommendationEventRepository;
import com.example.givy.domain.recommendation.repository.TendencyRepository;
import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import com.example.givy.domain.user.repository.UserSecuritiesAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeQueryServiceImpl implements HomeQueryService{

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final TendencyRepository tendencyRepository;
    private final UserSecuritiesAccountRepository userSecuritiesAccountRepository;
    private final RecommendationEventRepository recommendationEventRepository;

    private final KisStockService kisStockService;

    @Override
    public HomeResDTO.HomeResponseDTO getHome(Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        Tendency tendency = tendencyRepository.findByUsers(user).orElseThrow(() -> new TendencyException(TendencyErrorCode.TENDENCY_NOT_FOUND));

        // 진행 중인 챌린지를 우선 조회, 없으면 가장 최근 챌린지 조회
        Optional<StartChallenge> challengeOpt = challengeRepository.findFirstByUsersAndStatusOrderByCreatedAtDesc(user, Status.IN_PROGRESS)
                .or(() -> challengeRepository.findAllByUsers(user).stream()
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .findFirst());

        if (challengeOpt.isPresent()) {
            StartChallenge challenge = challengeOpt.get();
            Product product = challenge.getProduct();
            String productCode = product.getCode();

            // 가장 최근 증권 계좌 조회
            String securitiesName = userSecuritiesAccountRepository.findFirstByUsersOrderByCreatedAtDesc(user)
                    .map(account -> account.getSecuritiesName())
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_SECURITIES_ACCOUNT_NOT_FOUND));

            Long currentPrice = 0L;
            Double high52wPrice = 0.0;
            Double low52wPrice = 0.0;
            List<Long> chartData = Collections.emptyList();

            try {
                // A. 현재가 조회 (DTO: KisEtfPriceResponse -> currentPrice)
                KisEtfPriceResponse priceRes = kisStockService.getEtfPrice(productCode);
                if (priceRes != null && priceRes.getOutput() != null) {
                    currentPrice = Long.parseLong(priceRes.getOutput().getCurrentPrice());
                }

                // B. 52주 최고/최저가 조회 (DTO: KisStockInfoResponse -> highestPrice52w, lowestPrice52w)
                // 필드명 확인 완료 (highestPrice52w, lowestPrice52w)
                KisStockInfoResponse infoRes = kisStockService.getStockBasicInfo(productCode);
                if (infoRes != null && infoRes.getOutput() != null) {
                    high52wPrice = Double.parseDouble(infoRes.getOutput().getHighestPrice52w());
                    low52wPrice = Double.parseDouble(infoRes.getOutput().getLowestPrice52w());
                }

                // C. 차트 데이터 (일별 시세) 조회 (DTO: KisDailyPriceResponse -> closePrice)
                KisDailyPriceResponse dailyRes = kisStockService.getDailyPrices(productCode);
                if (dailyRes != null && dailyRes.getOutput() != null) {
                    // 최근 20개 정도만 잘라서 차트용으로 사용
                    chartData = dailyRes.getOutput().stream()
                            .limit(20)
                            .map(output -> Long.parseLong(output.getClosePrice()))
                            .collect(Collectors.toList());
                    // API는 최신순(내림차순)으로 줄 테니, 그래프를 위해 오름차순(과거->현재)으로 뒤집기
                    Collections.reverse(chartData);
                }

            } catch (Exception e) {
                log.error("KIS API 데이터 조회 실패: {}", e.getMessage());
                // API 호출 실패 시에도 화면은 띄워야 하므로 예외를 던지지 않고 기본값(0) 유지
            }

            // 3. 수익률 계산 (매수가 50,000원 고정)
            double purchasePrice = 50000.0;
            double profitRate = 0.0;
            int rateMax = 0;
            int rateMin = 0;

            if (purchasePrice > 0) {
                profitRate = ((currentPrice - purchasePrice) / purchasePrice) * 100;
                double denominator = (low52wPrice + high52wPrice) / 2.0 + currentPrice;

                if (denominator != 0) {
                    // 52주 최소 퍼센트
                    rateMin = (int) ((low52wPrice / denominator) * 100);

                    // 52주 최대 퍼센트
                    rateMax = (int) ((high52wPrice / denominator) * 100);
                }
            }

            // 4. Converter 호출 (데이터 전달)
            HomeResDTO.AfterHomeDTO afterData = HomeConverter.toAfterHomeDTO(
                    product, securitiesName, profitRate, rateMin, rateMax, chartData
            );

            return HomeConverter.toHomeResponseDTO(user, "AFTER_CHALLENGE", afterData);

        } else {
            RecommendationEvent event = recommendationEventRepository.findTopByTendencyOrderByCreatedAtDesc(tendency).orElseThrow(() -> new TendencyException(TendencyErrorCode.RECOMMENDATION_NOT_FOUND));

            Product bestProduct = event.getBestProduct();

            HomeResDTO.BeforeHomeDTO beforeDTO = HomeConverter.toBeforeHomeDTO(tendency, bestProduct);
            return HomeConverter.toHomeResponseDTO(user, "BEFORE_CHALLENGE", beforeDTO);
        }
    }
}
